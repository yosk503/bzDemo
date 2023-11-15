package com.example.demo.pmass.dao;

import com.example.demo.pmass.entity.PmPatchReg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface PMassDao extends JpaRepository<PmPatchReg, Long> {

    @Query(value = "SELECT PR.PATCH_CODE,\n" +
            "       PR.PATCH_DISC,\n" +
            "       PM.FILEBODY FILE_BODY,\n" +
            "       PM.FILE_NAME FILE_NAME,\n" +
            "       PR.PATCH_DEVER,\n" +
            "       PR.PATCH_FILES,\n" +
            "       PR.STAT\n" +
            "  FROM PM_ATTACH_FILES PM\n" +
            " INNER JOIN PM_PATCH_REG PR\n" +
            "    ON PM.BUS_ID = PR.PATCH_ID\n" +
            " WHERE PR.PATCH_CODE IN (:patchCode)",nativeQuery = true)
    List<Map<String,Object>> queryAllByPatchCode(@Param("patchCode") List<String> patchCode);

    @Modifying
    @Query(value = "UPDATE PM_PATCH_REG PM\n" +
            "   SET PM.STAT = :stat,PM.LAST_UPD_OPERID=:oper,PM.LAST_UPD_DATE=:date\n" +
            " WHERE  PM.PATCH_CODE IN (:patchCode)",nativeQuery = true)
    int updateStat(@Param("stat") String stat,@Param("oper") String oper,@Param("date") String date,@Param("patchCode") List<String> patchCode);

    @Query(value = "SELECT PATCH_CODE FROM PM_PATCH_REG WHERE PATCH_CODE IN (:patchCode)",nativeQuery = true)
    List<String > queryList(@Param("patchCode") List<String> patchCode);

    @Query(value = "SELECT PR.PATCH_CODE,\n" +
            "       PR.PATCH_DISC,\n" +
            "       PM.FILEBODY FILE_BODY,\n" +
            "       PM.FILE_NAME FILE_NAME,\n" +
            "       PR.PATCH_DEVER\n" +
            "  FROM PM_ATTACH_FILES PM,PR.PATCH_FILES\n" +
            " INNER JOIN PM_PATCH_REG PR\n" +
            "    ON PM.BUS_ID = PR.PATCH_ID\n" +
            " WHERE PR.PATCH_CODE IN (:patchCode)",nativeQuery = true)
    List<PmPatchReg> queryPatchCode(@Param("patchCode") List<String> patchCode);


    @Query(value = "SELECT T.MENU_ID, T.PATH_NAME\n" +
            "  FROM PUB_MENU_STRU T\n" +
            " WHERE T.PATH_NAME LIKE :name"+
            "   AND T.MENU_TYPE_ID = '1'",nativeQuery = true)
    List<Map<String,Object>> queryPubMenuIdLike(@Param("name") String name);
    @Query(value = "SELECT T.* FROM PUB_MODULES T\n" +
            " WHERE T.MODULE_CODE IN\n" +
            "       (SELECT B.MODULE_CODE\n" +
            "          FROM PUB_MENU_ITEM B\n" +
            "         WHERE B.MENU_ID IN\n" +
            "               (SELECT A.MENU_ID\n" +
            "                  FROM PUB_MENU_STRU A\n" +
            "                 WHERE MENU_TYPE_ID = '1'\n" +
            "                 START WITH MENU_ID = :menuId \n" +
            "                CONNECT BY NOCYCLE PRIOR MENU_ID = PARENT_MENU_ID))",nativeQuery = true)
    List<Map<String,Object>> queryPubModule(@Param("menuId") String menuId);

    @Query(value = "SELECT * FROM PUB_FUNCTIONS T\n" +
            "   WHERE T.FUNCTION_CODE IN\n" +
            "       (SELECT A.FUNCTION_CODE\n" +
            "        FROM PUB_MENU_ITEM A\n" +
            "       WHERE A.MENU_ID IN\n" +
            "           (SELECT T.MENU_ID\n" +
            "            FROM PUB_MENU_STRU T\n" +
            "           WHERE MENU_TYPE_ID = '1'\n" +
            "           START WITH MENU_ID = :menuId \n" +
            "          CONNECT BY NOCYCLE PRIOR MENU_ID = PARENT_MENU_ID))",nativeQuery = true)
    List<Map<String,Object>> queryPubFunction(@Param("menuId") String menuId);

    @Query(value ="SELECT *\n" +
            "  FROM PUB_OPERATIONS A\n" +
            " WHERE A.FUNCTION_CODE IN\n" +
            "       (SELECT A.FUNCTION_CODE\n" +
            "          FROM PUB_MENU_ITEM A\n" +
            "         WHERE A.MENU_ID IN\n" +
            "               (SELECT T.MENU_ID\n" +
            "                  FROM PUB_MENU_STRU T\n" +
            "                 WHERE MENU_TYPE_ID = '1'\n" +
            "                 START WITH MENU_ID = :menuId \n" +
            "                CONNECT BY NOCYCLE PRIOR MENU_ID = PARENT_MENU_ID))",nativeQuery = true)
    List<Map<String,Object>> queryPubOperation(@Param("menuId") String menuId);

    @Query(value ="SELECT * FROM PUB_URLS A\n" +
            " WHERE A.OPERATION_CODE IN\n" +
            "       (SELECT A.OPERATION_CODE\n" +
            "          FROM PUB_OPERATIONS A\n" +
            "         WHERE A.FUNCTION_CODE IN\n" +
            "               (SELECT A.FUNCTION_CODE\n" +
            "                  FROM PUB_MENU_ITEM A\n" +
            "                 WHERE A.MENU_ID IN\n" +
            "                       (SELECT T.MENU_ID\n" +
            "                          FROM PUB_MENU_STRU T\n" +
            "                         WHERE MENU_TYPE_ID = '1'\n" +
            "                         START WITH MENU_ID = :menuId \n" +
            "                        CONNECT BY NOCYCLE PRIOR MENU_ID = PARENT_MENU_ID)))",nativeQuery = true)
    List<Map<String,Object>> queryPubUrl(@Param("menuId") String menuId);

    @Query(value ="SELECT T.MENU_ID,\n" +
            "       T.MENU_NAME,\n" +
            "       T.REQUEST_ACTION,\n" +
            "       T.TARGET,\n" +
            "       T.FUNCTION_CODE,\n" +
            "       T.MODULE_CODE,\n" +
            "       T.APP_CODE,\n" +
            "       T.IS_LEAF,\n" +
            "       T.ICON\n" +
            "  FROM PUB_MENU_ITEM T\n" +
            " WHERE T.MENU_ID IN\n" +
            "       (SELECT A.MENU_ID\n" +
            "          FROM PUB_MENU_STRU A\n" +
            "         WHERE MENU_TYPE_ID = '1'\n" +
            "         START WITH MENU_ID = :menuId \n" +
            "        CONNECT BY NOCYCLE PRIOR MENU_ID = PARENT_MENU_ID)",nativeQuery = true)
    List<Map<String,Object>> queryPubMenuItem(@Param("menuId") String menuId);

    @Query(value ="SELECT T.MENU_STRU_ID,\n" +
            "       T.MENU_TYPE_ID,\n" +
            "       T.MENU_ID,\n" +
            "       T.PARENT_MENU_ID,\n" +
            "       T.MENU_PATH,\n" +
            "       T.PATH_NAME,\n" +
            "       T.SEQ\n" +
            "  FROM PUB_MENU_STRU T\n" +
            " WHERE MENU_TYPE_ID = '1'\n" +
            " START WITH MENU_ID = :menuId \n" +
            "CONNECT BY NOCYCLE PRIOR MENU_ID = PARENT_MENU_ID",nativeQuery = true)
    List<Map<String,Object>> queryPubMenuStar(@Param("menuId") String menuId);

    @Query(value = "SELECT * FROM PUB_DICT WHERE DICT_CODE = :dictCode",nativeQuery = true)
    List<Map<String, Object>> queryPubDict(@Param("dictCode") String dictCode);

    @Query(value = "SELECT * FROM PUB_DICT_ITEM WHERE DICT_CODE = :dictCode",nativeQuery = true)
    List<Map<String, Object>> queryPubDictItem(@Param("dictCode") String dictCode);

    @Query(value = "SELECT * FROM PUB_IDTABLE WHERE ID_ID= :id",nativeQuery = true)
    List<Map<String, Object>> queryPubIdTable(@Param("id") String id);

    @Query(value = "SELECT * FROM LSXXTS LS WHERE LS.LSXXTS_BH = = :id",nativeQuery = true)
    List<Map<String, Object>> queryLSXXTS(@Param("id") String id);

    @Query(value = "SELECT ATC.COLUMN_NAME,ATC.DATA_TYPE FROM ALL_TAB_COLUMNS ATC WHERE ATC.TABLE_NAME = :tableName",nativeQuery = true)
    List<Map<String, Object>> queryAllTable(@Param("tableName") String tableName);

    @Query(value = "SELECT COLUMN_NAME,COMMENTS FROM ALL_COL_COMMENTS WHERE TABLE_NAME = :tableName",nativeQuery = true)
    List<Map<String, Object>> queryAllComments(@Param("tableName") String tableName);

    @Query(value = "SELECT DBMS_METADATA.GET_DDL('TABLE', :tableName) FROM DUAL",nativeQuery = true)
    List<Object[]> queryTableStruct(@Param("tableName") String tableName);

    @Query(value = "SELECT COLUMN_NAME FROM ALL_CONS_COLUMNS WHERE TABLE_NAME = :tableName AND CONSTRAINT_NAME LIKE 'PK%'",nativeQuery = true)
    List<String> queryPrimaryKey(@Param("tableName") String tableName);

    @Query(value = "SELECT INDEX_NAME, COLUMN_NAME FROM ALL_IND_COLUMNS WHERE TABLE_NAME = :tableName",nativeQuery = true)
    List<Map<String,String>> queryIndex(@Param("tableName") String tableName);

    @Query(value = "SELECT DISTINCT COLUMN_NAME, COMMENTS FROM ALL_COL_COMMENTS WHERE TABLE_NAME  = :tableName",nativeQuery = true)
    List<Object[]> queryComment(@Param("tableName") String tableName);
}
