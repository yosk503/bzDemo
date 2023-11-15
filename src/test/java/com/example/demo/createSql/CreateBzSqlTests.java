package com.example.demo.createSql;

import com.example.demo.jpa.pMass.dao.PMassDao;
import com.example.demo.jpa.sitGm.dao.SitGmDao;
import com.example.demo.util.bzUtil.ExportTableHelp;
import com.example.demo.util.commonUtil.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.util.bzUtil.ExportTableHelp.createDictSql;
import static com.example.demo.util.bzUtil.ExportTableHelp.createTableSql;
import static com.example.demo.util.commonUtil.ConvertMapToObject.convertToCamelCase;
@Slf4j
@SpringBootTest
public class CreateBzSqlTests {
    @Autowired
    private PMassDao pMassDao;
    @Autowired
    private SitGmDao sitGmDao;
    Map<String,List<String>> mapList=new LinkedHashMap<>();


    /**
     * 框架启动测试方法
     * @throws Exception
     */
    @Test
    public void exportTableAndComment() throws Exception {
        //直营业务模式
//        exportTable("DIRECT_SALE_CONFIG");
//        exportTable("DIRECT_SALE_MODEL_CONFIG");
//        exportTable("DIRECT_SALE_DEALER_CONFIG");
//        exportTable("DIRECT_SALE_PRODUCT_CONFIG");
//        exportTable("DIRECT_SALE_RALATION");
//        exportDictCode("DIRECT_REFLECTION_TYPE");
//        exportDictCode("DIRECT_CONFIG_DICT");
//        exportDictCode("DIRECT_INVOICE_TYPE");


        //大设备需求
//        exportTable("DEV_CUST_BASE_INFO");
//        exportTable("DEV_HOLDER_BASE_INFO");
//        exportTable("DEV_MANAGER_BASE_INFO");
//        exportTable("DEV_LOAN_APPLY");
//        exportTable("DEV_LOAN_ITEMS_DETAIL");
//        exportTable("DEV_GRANT_APPLY");
//        exportTable("DEV_LOAN_REPAY_PLAN");
        queryMenuId("汽车金融日报报表");
//        exportMenu("000000000000000000000000004189");
        //直营数据映射
//        exportMenu("000000000000000000000000004284");
        //首先删除该文件夹下的所有文件
//        DeleteLogFile.findFileList(new File(Application.getProperty("create_class_dir")), new ArrayList<>(),new HashMap<>());
//        createEntityClass("DIRECT_SALE_ORDER_LOG");
//        createEntityClass("DIRECT_SALE_MODEL_CONFIG");
//        createEntityClass("DIRECT_SALE_DEALER_CONFIG");
        mapList.values().stream().flatMap(List::stream).forEach(System.out::println);
    }

    public static void main(String[] args) throws Exception {
//        createTableSql("D:\\fb\\sql.txt");
//        createDictSql("DIRECT_REFLECTION_TYPE","直营业务模式映射类型","直营业务模式映射类型字典",new String[]{"车型映射","网点映射"});
//        createDictSql("DIRECT_CONFIG_DICT","直营业务模式关联状态","直营业务模式关联状态字典",new String[]{"正常","作废"});
//        createDictSql("DIRECT_INVOICE_TYPE","直营业务模式开票类型","直营业务模式开票类型字典",new String[]{"厂端账户","交付中心账户"});
//        createDictSql("DEV_APPLY_STATE","登记状态","设备租赁信息登记状态字典",new String[]{"初始","待确认","已确认","退回","作废"});
//        createDictSql("DEV_GRANT_STATE","放款状态","设备租赁信息放款状态字典",new String[]{"初始","待确认","已确认","退回","作废"});
//        createDictSql("DEV_REPAY_TYPE","支付频率","设备租赁信息支付频率字典",new String[]{"按月支付","按季支付"});
//        createDictSql("DEV_REPAY_STATE","还款状态","设备租赁信息还款状态字典",new String[]{"未还款","已还款"});
//        createDictSql("DEV_REPAY_PLAN","还租方式","设备租赁信息还租方式字典",new String[]{"等额本息","等额本金","自定义"});
//        createDictSql("PART_REPAY_TYPE","部分提前还款还款类型","部分提前还款还款类型字典",new String[]{"以资抵债","SP部分代偿","SP全额代偿"});
//        createDictSql("PART_REPAY_STATE","单据状态","部分提前还款还款单据状态字典",new String[]{"制单","审核退回","审核中","复核通过","作废"});
//        createDictSql("PART_REPAY_TURN","部分提前还款还款顺序","部分提前还款还款顺序字典",new String[]{"本金","租息","罚息","本金|租息","本金|罚息","本金|租息|罚息","本金|罚息|租息"});
//         createDictSql("DIRECT_VENDOR_GROUP","直营模式厂商分组","直营模式厂商分组字典",new String[]{"A","B","C","D","E"});
        createDictSql("ITEM_MODEL_STATUS","车型车系品牌使用状态","车型车系品牌使用状态字典",new String[]{"停用","启用","初始"});
    }

    /**
     * 导出表，主键，索引，注释
     * 基于函数创建的索引暂时不能导出，导出的是地址，等待后续优化
     */
    public void exportTable(String tableName) throws Exception {
        tableName=tableName.trim().toUpperCase(Locale.ROOT);
        // 查询表结构
        List<Object[]> createTableResult =sitGmDao.queryTableStruct(tableName);
        List<String> tableList=mapList.get("table")==null?new ArrayList<>():mapList.get("table");
        ExportTableHelp.createTable(createTableResult,tableName,tableList);
        mapList.put("table",tableList);
        // 查询主键
        List<String> primaryKeys = sitGmDao.queryPrimaryKey(tableName);
        List<String> primaryList=mapList.get("primary")==null?new ArrayList<>():mapList.get("primary");
        String primaryKey=ExportTableHelp.createPrimaryKey(primaryKeys,tableName,primaryList);
        mapList.put("primary",primaryList);
        // 查询索引
        List<Map<String,String>> indexesResult = sitGmDao.queryIndex(tableName);
        List<String> indexList=mapList.get("index")==null?new ArrayList<>():mapList.get("index");
        ExportTableHelp.createIndex(indexesResult,primaryKey,tableName,indexList);
        mapList.put("index",indexList);
        // 查询注释
        List<Object[]> comments = sitGmDao.queryComment(tableName);
        List<String> commentList=mapList.get("comment")==null?new ArrayList<>():mapList.get("comment");
        ExportTableHelp.createComment(comments,tableName,commentList);
        mapList.put("comment",commentList);
    }
    /**
     * 导出字典表
     */
    public void exportDictCode(String dictCode){
        List<String> sqlList=mapList.get("dictCode")==null?new ArrayList<>():mapList.get("dictCode");
        ExportTableHelp.generateInsertSQL("PUB_DICT",sitGmDao.queryPubDict(dictCode),sqlList);
        ExportTableHelp.generateInsertSQL("PUB_DICT_ITEM",sitGmDao.queryPubDictItem(dictCode),sqlList);
        mapList.put("dictCode",sqlList);
    }


    /**
     * 根据中文名查询出对应的menuId
     */
    public void queryMenuId(String name){
        List<Map<String,Object>> list=sitGmDao.queryPubMenuIdLike("%"+name+"%");
        if(list.size()==1){
            String menuId=(String) list.get(0).get("MENU_ID");
            exportMenu(menuId);
        }else {
            List<String> convertedList = list.stream()
                    .map(map->map.get("MENU_ID")+","+map.get("PATH_NAME")) // 将每个 Map 转换为 JSON 字符串
                    .collect(Collectors.toList()); // 将转换后的字符串收集到新的 List 中
            mapList.put("menuId",convertedList);
        }

    }
    /**
     * 导出某个目录下的所有菜单
     * 000000000000000000000000004240 部分提前还款
     * 000000000000000000000000004189 大设备租赁
     */
    public void exportMenu(String menuId){
        List<String> sqlList=mapList.get("menuTable")==null?new ArrayList<>():mapList.get("menuTable");
        //模型表
        ExportTableHelp.generateInsertSQL("PUB_MODULES", sitGmDao.queryPubModule(menuId),sqlList);
        //功能表
        ExportTableHelp.generateInsertSQL("PUB_FUNCTIONS", sitGmDao.queryPubFunction(menuId),sqlList);
        //操作表
        ExportTableHelp.generateInsertSQL("PUB_OPERATIONS", sitGmDao.queryPubOperation(menuId),sqlList);
        //url表
        ExportTableHelp.generateInsertSQL("PUB_URLS", sitGmDao.queryPubUrl(menuId),sqlList);
        //节点表
        ExportTableHelp.generateInsertSQL("PUB_MENU_ITEM", sitGmDao.queryPubMenuItem(menuId),sqlList);
        //菜单表
        ExportTableHelp.generateInsertSQL("PUB_MENU_STRU", sitGmDao.queryPubMenuStar(menuId),sqlList);
        //SQL输出
        mapList.put("menuTable",sqlList);
    }
    /**
     * 导出PUB_TABLE表
     */
    public void exportIdTable(String id){
        List<String> sqlList=mapList.get("pubTable")==null?new ArrayList<>():mapList.get("pubTable");
        ExportTableHelp.generateInsertSQL("PUB_IDTABLE",sitGmDao.queryPubIdTable(id),sqlList);
        mapList.put("pubTable",sqlList);
    }
    /**
     * 导出首页提醒
     */
    public void exportLSSXXTS(String id){
        List<String> sqlList=mapList.get("lsxxts")==null?new ArrayList<>():mapList.get("lsxxts");
        ExportTableHelp.generateInsertSQL("LSXXTS",sitGmDao.queryLSXXTS(id),sqlList);
        mapList.put("lsxxts",sqlList);
    }


    /**
     * 从Word文档中生成表
     */
    public void  createTable() throws Exception {
        createTableSql("D:\\fb\\bzHelp\\sql.txt");
    }
    /**
     * 生成字典
     */
    public void createDictCode() throws Exception{
        createDictSql("DEV_APPLY_STATE","登记状态","设备租赁信息登记状态字典",new String[]{"初始","待确认","已确认","退回","作废"});
        createDictSql("DEV_GRANT_STATE","放款状态","设备租赁信息放款状态字典",new String[]{"初始","待确认","已确认","退回","作废"});
        createDictSql("DEV_REPAY_TYPE","支付频率","设备租赁信息支付频率字典",new String[]{"按月支付","按季支付"});
        createDictSql("DEV_REPAY_STATE","还款状态","设备租赁信息还款状态字典",new String[]{"未还款","已还款"});
        createDictSql("DEV_REPAY_PLAN","还租方式","设备租赁信息还租方式字典",new String[]{"等额本息","等额本金","自定义"});
        createDictSql("PART_REPAY_TYPE","部分提前还款还款类型","部分提前还款还款类型字典",new String[]{"以资抵债","SP部分代偿","SP全额代偿"});
        createDictSql("PART_REPAY_STATE","单据状态","部分提前还款还款单据状态字典",new String[]{"制单","审核退回","审核中","复核通过","作废"});
        createDictSql("PART_REPAY_TURN","部分提前还款还款顺序","部分提前还款还款顺序字典",new String[]{"本金","租息","罚息","本金|租息","本金|罚息","本金|租息|罚息","本金|罚息|租息"});
        createDictSql("PART_REPAY_TURN","部分提前还款还款顺序","部分提前还款还款顺序字典",new String[]{"本金|租息"});
    }
    /**
     * 连接数据库生成实体类 以及常用的类
     */
    public void createEntityClass(String tableName) throws Exception {
        //获取路径
        String path= Application.getProperty("create_class_dir");
        //生成实体类文件
        tableName=tableName.toUpperCase(Locale.ROOT);
        List<Map<String,Object>> tableList=sitGmDao.queryAllTable(tableName);
        List<Map<String,Object>> commentList=sitGmDao.queryAllComments(tableName);
        List<String> primaryKeys = sitGmDao.queryPrimaryKey(tableName);
        String entityClassName=convertToCamelCase(tableName).substring(0,1).toUpperCase(Locale.ROOT)+convertToCamelCase(tableName).substring(1);
        //生成实体类文件
        StringBuilder entityStringBuilder=ExportTableHelp.createEntity(tableList,commentList,primaryKeys,tableName);
        //生成dao类文件
        StringBuilder daoStringBuilder=ExportTableHelp.createDao(entityClassName);
        //生成queryCommand类文件
        StringBuilder queryCommandStringBuilder=ExportTableHelp.createQueryCommand(entityClassName,tableName);
        //生成command类文件
        StringBuilder commandStringBuilder=ExportTableHelp.createCommand(entityClassName,tableName);
        Map<String,StringBuilder> map=new HashMap<>();
        map.put(entityClassName,entityStringBuilder);
        map.put(entityClassName+"Dao",daoStringBuilder);
        map.put(entityClassName+"QueryCommand",queryCommandStringBuilder);
        map.put(entityClassName+"Command",commandStringBuilder);
        ExportTableHelp.createFile(map,path);
    }

}

