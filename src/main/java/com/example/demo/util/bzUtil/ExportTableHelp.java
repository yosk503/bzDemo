package com.example.demo.util.bzUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.SerializableClobProxy;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.util.commonUtil.ConvertMapToObject.convertToCamelCase;
@Slf4j
public class ExportTableHelp {
    /**
     * 处理创建表的语句
     */
    public static String filterCreateTableStatement(String createTableStatement, String tableName) {
        String returnSqlStart="CREATE TABLE "+tableName+"(\n";
        // 去除数据库用户名
        int startIndex =  createTableStatement.indexOf("(");
        String filteredStatement = createTableStatement.substring(startIndex+1);
        String[] sql=filteredStatement.split("CONSTRAINT");
        int endIndex=sql[0].lastIndexOf(",");
        String returnSql=sql[0].substring(0, endIndex);
        returnSql=returnSql.replace("\"","").replace("NUMBER(*,0)","INTEGER");
        returnSql=alignText(returnSql);
        return returnSqlStart+returnSql+");";
    }

    /**
     * 获取clob
     */
    public static String getClobString(Clob clob) throws Exception{
        String  object = null;
        try (Reader reader = clob.getCharacterStream()) {
            StringBuilder stringBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, bytesRead);
            }
            String content = stringBuilder.toString();
            // 或者将内容转换为字节数组
            // byte[] contentBytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
            object = content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * 格式化SQL

     */
    public static String alignText(String input) {
        // 拆分文本为每行
        String[] lines = input.split("\n");

        // 找到每行中的最长单词长度
        int maxLength = 0;
        for (String line : lines) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                if (word.length() > maxLength) {
                    maxLength = word.length();
                }
            }
        }

        // 对齐文本并输出
        StringBuilder alignedText = new StringBuilder();
        for (String line : lines) {
            String[] words = line.split("\\s+");
            StringBuilder alignedLine = new StringBuilder();
            for (String word : words) {
                String alignedWord = String.format("%-" + (maxLength+3) + "s", word);
                alignedLine.append(alignedWord).append(" ");
            }
            alignedText.append("    ").append(alignedLine.toString().trim()).append("\n");
        }

        return alignedText.toString();
    }

    /**
     * 构建插入语句
     */
    public static void generateInsertSQL(String tableName, List<Map<String, Object>> list, List<String> sqlList) {
        // 循环处理每一条数据
        for (Map<String,Object> data : list) {
            // 获取数据的键集合
            Set<String> keys = data.keySet();
            // 构建插入SQL语句的字段部分
            String columns = String.join(", ", keys);
            // 构建插入SQL语句的值部分
            String values = keys.stream()
                    .map(key -> formatValue(data.get(key)))
                    .collect(Collectors.joining(", "));
            // 构建完整的插入SQL语句
            String sql = "INSERT INTO " + tableName + " (" + columns + ")\n"+"VALUES (" + values + ");";
            // 将SQL语句添加到列表中
            sqlList.add(sql);
        }
    }

    /**
     * 生成创建表的SQL
     * COMMENT ON COLUMN LOAN_CALL_LOG.OUTER_ID  IS '唯一标识,主键';
     */
    public static void createTableSql(String path) throws Exception {
        BufferedReader bufferedReader;
        String primaryKey="";
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            String tableName=line;
            StringBuffer stringBuffer=new StringBuffer();
            while (line != null) {
                String lineNext = bufferedReader.readLine();
                line = line.trim();
                String[] arr = line.replaceAll("\\t", "。").replaceAll("（", "(").replaceAll("）", ")").split("。");
                String[] arrNext = new String[0];
                if (lineNext != null) {
                    arrNext = lineNext.replaceAll("\\t", "。").replaceAll("（", "(").replaceAll("）", ")").split("。");
                }
                arr = Arrays.stream(arr).map(s -> s.replaceAll("\\s+", "")).toArray(String[]::new);
                if (arr.length == 1) {
                    tableName=arr[0];
                    System.out.println("CREATE TABLE " + arr[0]);
                    System.out.println("(");
                } else {
                    if (arrNext.length == 1||lineNext==null) {
                        System.out.printf("  %-25s %s%n", arr[0], arr[1] );
                        stringBuffer.append(" COMMENT ON COLUMN ").append(tableName).append(".").append(arr[0]).append(" IS '").append(arr[3]).append("';\n");
                    } else {
                        System.out.printf("  %-25s %s,%n", arr[0], arr[1]);
                        stringBuffer.append(" COMMENT ON COLUMN ").append(tableName).append(".").append(arr[0]).append(" IS '").append(arr[3]).append("';\n");
                    }
                }
                if(arrNext.length==1||lineNext==null){
                    System.out.println(");");
                }
                line = lineNext;
            }

            System.out.println(stringBuffer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }

    /**
     * 生成字典项的SQL
     * insert into pub_dict (DICT_CODE, DICT_NAME, SET_MAN, SET_TIME, NOTE, IN_USE, IS_DEV)
     * values ('CALL_TYPE', '呼叫类型', 'ADMIN', '20230220 17:24:02', '呼叫中心呼叫类型字典', '1', '0');
     * insert into pub_dict_item (ITEM_CODE, DICT_CODE, ITEM_VALUE, XH, PARENT_CODE, NOTE, IN_USE)
     * values ('1', 'CALL_TYPE', '拨打电话', 0, null, null, '1');
     * insert into pub_dict_item (ITEM_CODE, DICT_CODE, ITEM_VALUE, XH, PARENT_CODE, NOTE, IN_USE)
     * values ('2', 'CALL_TYPE', '接听电话', 1, null, null, '1');
     * insert into pub_dict_item (ITEM_CODE, DICT_CODE, ITEM_VALUE, XH, PARENT_CODE, NOTE, IN_USE)
     * values ('3', 'CALL_TYPE', '转接电话', 1, null, null, '1');
     */
    public static void createDictSql(String dictCode, String dictName, String note, String[] itemValue) throws Exception{
        int mm=0;
        String nowDate=new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        System.out.println("INSERT INTO PUB_DICT (DICT_CODE, DICT_NAME, SET_MAN, SET_TIME, NOTE, IN_USE, IS_DEV)");
        System.out.printf("VALUES ('%s', '%s', 'ADMIN', '%s', '%s', '1', '0');",dictCode,dictName,nowDate,note);
        System.out.println();
        for (int i = 0; i < itemValue.length; i++) {
            System.out.println("INSERT INTO PUB_DICT_ITEM (ITEM_CODE, DICT_CODE, ITEM_VALUE, XH, PARENT_CODE, NOTE, IN_USE)");
            System.out.printf("VALUES ('%s', '%s', '%s', %s, NULL, NULL, '1');",String.format("%d", mm),dictCode,itemValue[i],i);
            System.out.println();
            mm++;
        }
    }

    /**
     * value优化
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String) {
            return "'" + value.toString() + "'";
        } else {
            return value.toString();
        }
    }

    /**
     * 查询表结构
     */
    public static List<String> createTable(List<Object[]> createTableResult,String tableName, List<String> tableList) throws Exception {
        if (!createTableResult.isEmpty()) {
            Object[] objectArray=createTableResult.get(0);
            Object object=objectArray[0];
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
            if (invocationHandler instanceof SerializableClobProxy) {
                SerializableClobProxy serializableClobProxy = (SerializableClobProxy) invocationHandler;
                Clob clob=serializableClobProxy.getWrappedClob();
                if (clob != null) {
                    object= ExportTableHelp.getClobString(clob);
                }
            }
            object=ExportTableHelp.filterCreateTableStatement((String) object,tableName);
            tableList.add((String) object);
        }
        return tableList;
    }

    /**
     * 查询主键
     */
    public static String  createPrimaryKey(List<String> primaryKeys,String tableName,List<String> primaryList){
        String primaryKey="";
        if (!primaryKeys.isEmpty()) {
            primaryKey = primaryKeys.get(0);
            String primary="ALTER TABLE " + tableName + " ADD CONSTRAINT " + "PK_" + tableName + " PRIMARY KEY (" + primaryKey+");";
            primaryList.add(primary);
        }
        return primaryKey;
    }

    /**
     * 查询索引
     */
    public static List<String> createIndex(List<Map<String,String>> indexesResult,String primaryKey,String tableName,List<String> indexList){
        //对索引去重
        Set<String> keys = new HashSet<>();
        List<Map<String, String>> deduplicatedList = new ArrayList<>();
        for (Map<String, String> map : indexesResult) {
            String key = map.get("INDEX_NAME");
            if (!keys.contains(key)) {
                keys.add(key);
                deduplicatedList.add(map);
            }
        }

        for (int i = 0; i < deduplicatedList.size(); i++) {
            String indexName = deduplicatedList.get(i).get("INDEX_NAME");
            String columnName =deduplicatedList.get(i).get("COLUMN_NAME");
            if(!columnName.equals(primaryKey)){
                String indexSql="CREATE INDEX " + indexName + " ON " + tableName + " (" + columnName + ");";
                indexList.add(indexSql);
            }
        }
        return indexList;
    }

    /**
     * 查询注释
     */
    public static List<String> createComment(List<Object[]> comments,String tableName,List<String> commentList){
        for (Object[] index : comments) {
            String columnName = (String) index[0];
            String comment = ((String) index[1]);
            if(StringUtils.isNotEmpty(comment)){
                comment=comment.replace("\n","");
            }else {
                comment="";
            }
            String commentsSql="COMMENT ON COLUMN " + tableName + "." + columnName + " IS '" + comment + "';";
            commentList.add(commentsSql);
        }
        return commentList;
    }

    /**
     * 生成实体类的字段
     */
    public static String generateField(String columnName, String dataType, String columnComment) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("\t/**\n");
        sb.append("\t* ").append(columnComment).append("\n");
        sb.append("\t*/\n");
        sb.append("\t@Column(name = \"").append(columnName).append("\")\n");
        sb.append("\tprivate ");
        sb.append(getFieldType(dataType));
        sb.append(" ");
        sb.append(convertToCamelCase(columnName)).append(";");
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 获取字段类型
     */
    private static String getFieldType(String dataType) {
        // 根据数据库字段类型映射Java数据类型，你可以根据需要进行修改
        if (dataType.equalsIgnoreCase("int")) {
            return "int";
        } else if (dataType.equalsIgnoreCase("varchar")) {
            return "String";
        } else if (dataType.equalsIgnoreCase("datetime")) {
            return "Date";
        } else if (dataType.equalsIgnoreCase("number")) {
            return "BigDecimal";
        } else {
            return "String";
        }
    }

    /**
     * 生成实体类 class类代码
     */
    public static String generateEntityClass(String className, List<String> fields, List<String> getGenerate) {
        StringBuilder sb = new StringBuilder();
        sb.append("public class ");
        String classNameStart=className.substring(0,1).toUpperCase(Locale.ROOT);
        String classNameEnd=className.substring(1);
        sb.append(classNameStart).append(classNameEnd);
        sb.append(" extends StatefulDatabean{\n");
        for (String field : fields) {
            sb.append(field);
        }
        sb.append("\n");
        for (String field : getGenerate) {
            sb.append(field);
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 生成导入的包相关代码
     */
    public static String getImport(LinkedList<TableEntityClass> classList){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("import org.loushang.next.dao.Column;\n" +
                            "import org.loushang.next.dao.Table;\n" +
                            "import org.loushang.next.data.StatefulDatabean;\n\n");
        List<String>  dateType = classList.stream()
                .map(TableEntityClass::getDataType)
                .distinct()
                .collect(Collectors.toList());
        for (int i = 0; i < dateType.size(); i++) {
            String type = dateType.get(i);
            if (type.equalsIgnoreCase("number")) {
                stringBuffer.append("import java.math.BigDecimal;\n");
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 生成实体类的get set方法
     */
    public static String generate(String columnName, String dataType){
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("\tpublic ").append(getFieldType(dataType)).append(" ").append(convertToCamelCase("get_"+columnName)).append("() {\n");
        stringBuffer.append("\t\treturn ").append(convertToCamelCase(columnName)).append(";\n").append("\t}\n\n");
        stringBuffer.append("\tpublic void ").append(convertToCamelCase("set_"+columnName)).append("(").append(getFieldType(dataType)).append(" ").append(convertToCamelCase(columnName)).append(") {\n");
        stringBuffer.append("\t\tthis.").append(convertToCamelCase(columnName)).append(" = ").append(convertToCamelCase(columnName)).append(";\n");
        stringBuffer.append("\t}\n\n");
        return stringBuffer.toString();
    }

    /**
     * 合并生成class对象
     */
    public static LinkedList<TableEntityClass> getClassList(List<Map<String,Object>> tableList,  List<Map<String,Object>> commentList){
        LinkedList<TableEntityClass> classList=new LinkedList<>();
        for (int i = tableList.size()-1; i >=0; i--) {
            Map<String,Object> tableMap=tableList.get(i);
            TableEntityClass tableEntityClass=new TableEntityClass();
            for (int j = 0; j < commentList.size(); j++) {
                Map<String,Object> commentMap=commentList.get(j);
                if(tableMap.get("COLUMN_NAME").equals(commentMap.get("COLUMN_NAME"))){
                    tableEntityClass.setColumnName((String) tableMap.get("COLUMN_NAME"));
                    tableEntityClass.setDataType((String) tableMap.get("DATA_TYPE"));
                    tableEntityClass.setComments((String) commentMap.get("COMMENTS"));
                    classList.add(tableEntityClass);
                }
            }
        }
        return classList;
    }

    /**
     * 生成主键，表名相关的代码
     */
    public static String getPrimaryKeySql(List<String> primaryKeys,String tableName){
        StringBuilder stringBuffer=new StringBuilder();
        stringBuffer.append("\n\n");
        stringBuffer.append("/**\n" )
                .append("* @Author 系统自动生成\n")
                .append("* @Date ")
                .append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n")
                .append("*/\n");
        if(primaryKeys!=null&&primaryKeys.size()>0){
            stringBuffer.append("@Table(tableName = \"").append(tableName).append("\",keyFields =\"").append(primaryKeys.get(0)).append("\")\n");
        }else {
            stringBuffer.append("@Table(tableName = \"").append(tableName).append("\",\n");
        }
        return stringBuffer.toString();
    }

    /**
     * 生成实体类字段 相关代码
     */
    public static String getColumnSql(LinkedList<TableEntityClass> classList,String tableName){
        LinkedList<String> getGenerate = new LinkedList<>();
        LinkedList<String> fields = new LinkedList<>();
        for (TableEntityClass entity : classList) {
            String field = ExportTableHelp.generateField(entity.getColumnName(), entity.getDataType(), entity.getComments());
            String generate=ExportTableHelp.generate(entity.getColumnName(),entity.getDataType());
            fields.add(field);
            getGenerate.add(generate);
        }
        return ExportTableHelp.generateEntityClass(convertToCamelCase(tableName), fields,getGenerate);
    }

    /**
     * 生成实体类文件的方法
     */
    public static StringBuilder createEntity(List<Map<String,Object>> tableList,List<Map<String,Object>> commentList,List<String> primaryKeys,String tableName){
        LinkedList<TableEntityClass> classList=ExportTableHelp.getClassList(tableList,commentList);
        //获取对应的包
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(ExportTableHelp.getImport(classList));
        stringBuilder.append(ExportTableHelp.getPrimaryKeySql(primaryKeys,tableName));
        //获取实体类字段
        stringBuilder.append(ExportTableHelp.getColumnSql(classList,tableName));
        return stringBuilder;
    }

    /**
     * 生成dao类文件
     */
    public static StringBuilder createDao(String entityClassName){
        String daoClassName=entityClassName+"Dao";
        StringBuilder daoStringBuilder=new StringBuilder();
        daoStringBuilder.append("package com.genersoft.cfs.indiv.partRepay.dao;").append("\n\n");
        daoStringBuilder.append("import com.genersoft.cfs.common.dao.TsBaseJdbcDao;").append("\n\n");
        daoStringBuilder.append("/**\n" ).append("* @Author 系统自动生成\n").append("* @Date ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n").append("*/\n");
        daoStringBuilder.append("public class").append(" ").append(daoClassName).append(" extends TsBaseJdbcDao<").append(entityClassName).append("> {").append("\n\n");
        daoStringBuilder.append("\t").append("public ").append(daoClassName).append("() throws Exception {").append("\n").append("\t").append("}").append("\n\n");
        daoStringBuilder.append("\t").append("public ").append(daoClassName).append("(Class entityClass) throws Exception {").append("\n");
        daoStringBuilder.append("\t\t").append(" super(entityClass);").append("\n");
        daoStringBuilder.append("\t").append("}").append("\n");
        daoStringBuilder.append("}");
        return daoStringBuilder;
    }

    /**
     * 生成queryCommand类文件
     */
    public static StringBuilder createQueryCommand(String entityClassName,String tableName){
        String queryCmdClassName=entityClassName+"QueryCommand";
        String daoClassName=entityClassName+"Dao";
        StringBuilder queryCmdStringBuilder=new StringBuilder();
        queryCmdStringBuilder.append("package com.genersoft.cfs.indiv.partRepay.dao;").append("\n\n");
        queryCmdStringBuilder.append("import com.genersoft.cfs.common.dao.TsDaoFactory;").append("\n");
        queryCmdStringBuilder.append("import org.loushang.next.web.cmd.BaseQueryCommand;").append("\n\n");
        queryCmdStringBuilder.append("/**\n" ).append("* @Author 系统自动生成\n").append("* @Date ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n").append("*/\n");
        queryCmdStringBuilder.append("public class ").append(queryCmdClassName).append(" extends BaseQueryCommand {").append("\n\n");
        queryCmdStringBuilder.append("\t").append("private static final ").append(daoClassName).append(" ").append(convertToCamelCase(tableName)).append("Dao = (").append(daoClassName).append(")").append("\n");
        queryCmdStringBuilder.append("\t\t").append("TsDaoFactory.getDao(\"").append("\"").append(",").append(entityClassName).append(".class").append(");").append("\n");
        queryCmdStringBuilder.append("}");
        return queryCmdStringBuilder;
    }

    /**
     * 生成Command类文件
     */
    public static StringBuilder createCommand(String entityClassName,String tableName){
        String cmdClassName=entityClassName+"Command";
        String daoClassName=entityClassName+"Dao";
        StringBuilder cmdStringBuilder=new StringBuilder();
        cmdStringBuilder.append("package com.genersoft.cfs.indiv.partRepay.dao;").append("\n\n");
        cmdStringBuilder.append("import com.genersoft.cfs.common.dao.TsDaoFactory;").append("\n");
        cmdStringBuilder.append("import org.loushang.next.web.cmd.BaseAjaxCommand;").append("\n\n");
        cmdStringBuilder.append("/**\n" ).append("* @Author 系统自动生成\n").append("* @Date ").append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append("\n").append("*/\n");
        cmdStringBuilder.append("public class ").append(cmdClassName).append(" extends BaseAjaxCommand {").append("\n\n");
        cmdStringBuilder.append("\t").append("private static final ").append(daoClassName).append(" ").append(convertToCamelCase(tableName)).append("Dao = (").append(daoClassName).append(")").append("\n");
        cmdStringBuilder.append("\t\t").append("TsDaoFactory.getDao(\"").append("\"").append(",").append(entityClassName).append(".class").append(");").append("\n");
        cmdStringBuilder.append("}");
        return cmdStringBuilder;
    }

    /**
     * 生成文件
     */
    public static void createFile(Map<String,StringBuilder> map,String path) throws Exception {
        for (String fileName:map.keySet()){
            FileWriter writer = new FileWriter(path+getPath(fileName)+fileName+".java");
            writer.write(map.get(fileName).toString());
            writer.close();
            log.info(fileName+".java"+"   ---文件生成成功！");
        }
    }

    /**
     * 获取文件夹路径
     */
    public static String getPath(String fileName){
        if(fileName.contains("Command")){
            return "cmd\\";
        }else if(fileName.contains("Dao")){
            return "Dao\\";
        }else {
            return "Entity\\";
        }
    }
}
