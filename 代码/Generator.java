package com.jfeat;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.jfeat.mojo.CRUDPojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by jackyhuang on 16/10/26.
 */
public class Generator {

    public final String mysqlDatabaseDriverName = "com.mysql.jdbc.Driver";
    public final String h2memDatabaseDriverName = "org.h2.Driver";
    public final String sqliteDatabaseDriverName = "org.sqlite.JDBC";

    private boolean initialize;

    /*private String[] updateTables;

    public String[] getUpdateTables() {
        return updateTables;
    }
    private boolean updateOverall = false;

    public boolean getUpdateOverall(){return updateOverall;}
    public void setUpdateOverall(boolean overall){updateOverall=overall;}

    public void setUpdateTables(String[] updateTables) {
        this.updateTables = updateTables;
    }*/

    public void setInitialize(boolean initialize) {
        this.initialize = initialize;
    }

    public boolean isInitialize() {
        return initialize;
    }

    public boolean isSkipTest() {
        return skipTest;
    }

    public void setSkipTest(boolean skipTest) {
        this.skipTest = skipTest;
    }

    private CRUDPojo[] cruds = null;

    public String[] getTables() {
        return includedTables;
    }

    public void setTables(String[] tables) {
        this.includedTables = tables;
    }

    private String outputDir = "src/main/java".replace('/', File.separatorChar);
    private String testOutputDir = "src/test/java".replace('/', File.separatorChar);

    private String moduleName = null;
    private String parentModuleName = null;

    public String getOutputDir() {
        return outputDir;
    }

    private String Author = "Code Generator";
    private boolean fileOverride = false;

    private boolean skipTest = false;

    private String databaseDriverName = mysqlDatabaseDriverName;
    private String databaseUsername = "";
    private String databasePassword = "";
    private String databaseUrl = "jdbc:mysql://127.0.0.1/test?user=root&password=root&characterEncoding=utf8";

    private String[] includedTables = new String[]{};
    private String deleteFieldName = "delete_flag";

    private String parentPackage = "com.jfeat.am.module";
    private String entityPackage = "services.gen.persistence.model";

    // CRUD
    private String mapperPackage = "services.gen.persistence.dao";
    private String xmlPackage = "services.gen.persistence.dao.mapping";
    private String servicePackage = "services.gen.crud.service";
    private String serviceImplPackage = "services.gen.crud.service.impl";
    private final String crudModelPackage = "services.gen.crud.model";

    /// DOMAIN
    private final String domainPackage = "services.domain";

    private final String crudFilterPackage = "services.domain.filter";
    private final String domainRecordPackage = "services.domain.model";
    //private final String domainModelPackage = "services.domain.model";


    // API
    private String controllerPackage = "api";

    //private String parentPath = parentPackage.replace('.', File.separatorChar);

    private String getModulePackagePath() {
        return outputDir + File.separator + parentPackage.replace('.', File.separatorChar) + File.separator + moduleName;
    }

    private String getEntityPackagePath(){
        return getModulePackagePath() + File.separatorChar + entityPackage.replace(".", File.separator);
    }

   /*public String getMapperPackagePath() {
        return getModulePackagePath() + File.separator +  mapperPackage.replace(".", File.separator);;
    }*/

    private String getCRUDFilterPackagePath() {
        return getModulePackagePath() + File.separatorChar + crudFilterPackage.replace('.', File.separatorChar);
    }

    private String getCRUDModelPackagePath() {
        return getModulePackagePath() + File.separatorChar + crudModelPackage.replace('.', File.separatorChar);
    }

    public String getDomainPackagePath() {
        return getModulePackagePath() + File.separatorChar + domainPackage.replace('.', File.separatorChar);
    }

    public String getDomainRecordPackagePath() {
        return getModulePackagePath() + File.separatorChar + domainRecordPackage.replace('.', File.separatorChar);
    }

    /*public String getDomainModelPackagePath() {
        return getModulePackagePath() + File.separatorChar + domainModelPackage.replace('.', File.separatorChar);
    }*/

    /*public String getPatchPackage() {
        return patchpackage;
    }

    public void setPatchPackage(String patchpackage) {
        this.patchpackage = patchpackage;
    }*/
    private String getTestModulePackagePath() {
        return testOutputDir + File.separator + parentPackage.replace('.', File.separatorChar) + File.separator + moduleName;
    }

    ///
    private AutoGenerator mpg = new AutoGenerator();

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir.replace('/', File.separatorChar);
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public void setFileOverride(boolean fileOverride) {
        this.fileOverride = fileOverride;
    }

    public void setDatabaseDriverName(String databaseDriverName) {
        this.databaseDriverName = databaseDriverName;
    }

    public void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public void setIncludedTables(String[] includedTables) {
        this.includedTables = includedTables;
    }

    public void setDeleteFieldName(String deleteFieldName) {
        this.deleteFieldName = deleteFieldName;
    }

    public void setParentPackage(String parentPackage) {
        this.parentPackage = parentPackage;

        /// set parent module name
        parentModuleName = parentPackage.substring(parentPackage.lastIndexOf('.') + 1);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setEntityPackage(String entityPackage) {
        this.entityPackage = entityPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }

    public void setXmlPackage(String xmlPackage) {
        this.xmlPackage = xmlPackage;
    }

    public void setServicePackage(String servicePackage) {
        this.servicePackage = servicePackage;
    }

    public void setServiceImplPackage(String serviceImplPackage) {
        this.serviceImplPackage = serviceImplPackage;
    }

    public void setControllerPackage(String controllerPackage) {
        this.controllerPackage = controllerPackage;
    }

    public CRUDPojo[] getCruds() {
        return cruds;
    }

    public void setCruds(CRUDPojo[] cruds) {
        this.cruds = cruds;
    }

    public void executeBulk(Log systemLog) throws IOException {

        try {
            if (cruds != null && cruds.length > 0) {
                CRUDPojo[] cloneCruds = cruds.clone();

                for (CRUDPojo pojo : cloneCruds) {

                    String[] cloneTables = this.includedTables == null ? null : this.includedTables.clone();

                    this.includedTables = cloneTables;
                    if (this.includedTables == null || this.includedTables.length == 0) {
                        // get include tables from crud
                        ArrayList<String> tableList = new ArrayList<>();

                        if (CRUDPojo.MASTER.equalsIgnoreCase(pojo.getMask())) {
                            systemLog.info("CRUD?????????Master,?????????includedTables?????????" + pojo.getMaster());
                            if (pojo.getMaster() != null) tableList.add(pojo.getMaster());
                            // do not generate slave serviceOnly
                            //if (crud.getSlaves() != null) tableList.addAll(crud.getSlaves());
                            //if (crud.getChildren() != null) tableList.addAll(crud.getChildren());
                        }
                        if (CRUDPojo.GROUP.equalsIgnoreCase(pojo.getMask())) {
                            /// if group by is set, only groupby table included.
                            if (pojo.getGroupBy() != null){
                                systemLog.info("CRUD?????????Group,?????????includedTables?????????" + pojo.getGroupBy());
                                tableList.add(pojo.getGroupBy());

                            }else if(pojo.getGroup() != null){
                                systemLog.info("CRUD?????????Group,?????????includedTables?????????" + pojo.getGroup());
                                tableList.add(pojo.getGroup());
                            }
                        }
                        if (CRUDPojo.PEER.equalsIgnoreCase(pojo.getMask())) {
                            systemLog.info("CRUD?????????Peer,?????????includedTables?????????" + pojo.getMaster());
                            if (pojo.getMaster() != null) tableList.add(pojo.getMaster());
                            systemLog.info("CRUD?????????Peer,?????????includedTables?????????" + pojo.getMasterPeer());
                            if (pojo.getMasterPeer() != null) tableList.add(pojo.getMasterPeer());
                            systemLog.info("CRUD?????????Peer,?????????includedTables?????????" + pojo.getRelation());
                            if (pojo.getRelation() != null) tableList.add(pojo.getRelation());
                        }

                        if (tableList.size() > 0) {
                            includedTables = new String[tableList.size()];
                            tableList.toArray(includedTables);

                        } else {
                            systemLog.error("???????????????????????????!");
                            throw new RuntimeException("???????????????????????????!");
                        }

                    }
                    cruds = new CRUDPojo[]{pojo};
                    execute(systemLog);

                    // master only when includeTables not provided
                    // TODO, fail to generate more then one times.
                    if(false) {
                        if (cloneTables == null || cloneTables.length == 0) {

                            if (CRUDPojo.MASTER.compareTo(pojo.getMask()) == 0 &&
                                    ((pojo.getSlaves() != null && pojo.getSlaves().size() > 0) ||
                                            (pojo.getChildren() != null && pojo.getChildren().size() > 0))
                                    ) {

                                ArrayList<String> tableList = new ArrayList<>();
                                if (pojo.getSlaves() != null) {
                                    tableList.addAll(pojo.getSlaves());
                                }
                                if (pojo.getChildren() != null) {
                                    tableList.addAll(pojo.getChildren());
                                }

                                includedTables = new String[tableList.size()];
                                tableList.toArray(includedTables);

                                cruds = null;
                                execute(systemLog);
                            }
                        }
                    }

                }// for each crud

            } else {
                // no cruds
                execute(systemLog);
            }

        } catch (Exception e) {
            systemLog.error(e);
        }
    }


        /**
         * for debug
         *
         * @param args
         * @throws Exception
         */

    public static void main(String[] args) throws Exception {
        Generator generator = new Generator();
        generator.setOutputDir("out");
        if (generator.outputDir != null && generator.outputDir.length() > 0) {
            generator.setOutputDir(generator.outputDir.replace("/", File.separator));
        }

        /// remote debug
        //generator.setDatabaseUrl(generator.databaseUrl);
        generator.setDatabaseUrl("jdbc:mysql://ok.zele.pro/power?user=root&password=root&characterEncoding=utf8");
        generator.setModuleName("cg");
        generator.setSkipTest(true);
        generator.setInitialize(false);
        //generator.setTables(new String[]{"cg_master_resource", "cg_master_resource_item", "cg_master_resource_record"});
        generator.setIncludedTables(new String[]{"cg_master_resource"});

        /// test CRUD
        CRUDPojo pojo = new CRUDPojo();
        pojo.setMask(CRUDPojo.MASTER);
        pojo.setMaster("cg_master_resource");
        //pojo.setMasterId("master_id;other_id");
        //pojo.setSlaves(Arrays.asList(new String[]{"cg_master_resource_item","cg_master_resource_record"}));

        /*CRUDPojo pojo1 = new CRUDPojo();
        pojo1.setMask(CRUDPojo.MASTER);
        pojo1.setMaster("cg_master_resource_item");
        CRUDPojo pojo2 = new CRUDPojo();
        pojo2.setMask(CRUDPojo.MASTER);
        pojo2.setMaster("cg_master_resource_record");*/

        //pojo.setMask(CRUDPojo.GROUP);
        //pojo.setGroup("cg_master_resource_category");
        //pojo.setGroupBy("cg_master_resource");
        //pojo.setGroupId("category_id");

        /// finally
        generator.executeBulk(new SystemStreamLog());
    }

    public void execute(Log log) throws IOException {

        ///???????????????????????????????????? persistence
        if (initialize) {
            log.info("????????????????????????..");
            this.setFileOverride(true);

            try {
                /// update to database
                sqlToDataBase(log, this.databaseUrl, this.databaseUsername, this.databasePassword, this.databaseDriverName);
            } catch (Exception e) {
                log.error(e.toString());
                throw new RuntimeException(e);
            }
        }

        /// ????????????
        String[] tablePrefix = getTablePrefix(this.databaseUrl,
                this.databaseUsername,
                this.databasePassword,
                this.databaseDriverName);

        // ????????????
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(this.outputDir);
        globalConfig.setFileOverride(this.fileOverride);
        globalConfig.setActiveRecord(true);
        globalConfig.setEnableCache(false);// XML ????????????
        globalConfig.setBaseResultMap(true);// XML ResultMap
        globalConfig.setBaseColumnList(true);// XML columList
        globalConfig.setAuthor(this.Author);

        // ?????????????????????????????? %s ?????????????????????????????????
        // globalConfig.setMapperName("%sDao");
        // globalConfig.setXmlName("%sDao");
        globalConfig.setServiceName("CRUD%sService");
        globalConfig.setServiceImplName("CRUD%sServiceImpl");
        globalConfig.setControllerName("%sEndpoint");

        ///??????Master??????Slaves???Children, ??????????????????
        if (cruds != null && cruds.length == 1 && cruds[0].getMaster() != null && cruds[0].getMaster().length() > 0 &&
                ((cruds[0].getSlaves() != null && cruds[0].getSlaves().size() > 0) || (cruds[0].getChildren() != null && cruds[0].getChildren().size() > 0))) {
            globalConfig.setServiceName("CRUD%sOverModelService");
            globalConfig.setServiceImplName("CRUD%sOverModelServiceImpl");
            globalConfig.setControllerName("%sOverModelEndpoint");
        }
        //??????GroupBy, ??????????????????
        else if (cruds != null && cruds.length == 1 && cruds[0].getGroupBy() != null && cruds[0].getGroupBy().length() > 0) {
            globalConfig.setServiceName("CRUD%sGroupByService");
            globalConfig.setServiceImplName("CRUD%sGroupByServiceImpl");
            globalConfig.setControllerName("%sGroupByEndpoint");
        }
        //??????Group, ??????????????????
        else if (cruds != null && cruds.length == 1 && cruds[0].getGroup() != null && cruds[0].getGroup().length() > 0) {
            if(cruds[0].getGroup().endsWith("_category")){
                // pass
            }else if(cruds[0].getGroup().endsWith("_type")){
                // pass
            }else {
                globalConfig.setServiceName("CRUD%sGroupService");
                globalConfig.setServiceImplName("CRUD%sGroupServiceImpl");
                globalConfig.setControllerName("%sGroupEndpoint");
            }
        }

        mpg.setGlobalConfig(globalConfig);


        // ???????????????
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);
        dataSourceConfig.setTypeConvert(new MySqlTypeConvert() {
            // ???????????????????????????????????????????????????
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                // ????????????processTypeConvert ???????????????????????????????????????????????????????????????????????????????????????????????????
                return super.processTypeConvert(fieldType);
            }
        });
        dataSourceConfig.setDriverName(this.databaseDriverName);
        if (this.databaseUsername != null && this.databaseUsername.length() > 0) {
            dataSourceConfig.setUsername(this.databaseUsername);
        }
        if (this.databasePassword != null && this.databasePassword.length() > 0) {
            dataSourceConfig.setPassword(this.databasePassword);
        }
        dataSourceConfig.setUrl(this.databaseUrl);
        mpg.setDataSource(dataSourceConfig);

        // ????????????
        StrategyConfig strategy = new StrategyConfig();
        // strategy.setCapitalMode(true); // ?????????????????? ORACLE ??????
        strategy.setTablePrefix(tablePrefix); // ????????????????????????????????????
        strategy.setNaming(NamingStrategy.underline_to_camel); // ??????????????????
        strategy.setLogicDeleteFieldName(this.deleteFieldName); // ??????????????????
        if (this.includedTables != null && this.includedTables.length > 0) {
            strategy.setInclude(this.includedTables); // ??????????????????
        } else {
            log.error("???????????? includeTables");
            throw new RuntimeException("???????????? includeTables");
        }

        //strategy.setExclude(this.excludedTables); // ??????????????????
        // ?????????????????????
        // strategy.setSuperEntityClass("com.baomidou.demo.TestEntity");
        // ??????????????????????????????
        // strategy.setSuperEntityColumns(new String[] { "test_id", "age" });
        // ????????? mapper ??????
        // strategy.setSuperMapperClass("com.baomidou.demo.TestMapper");

        // ????????? service ??????
        // TODO, no default, check if ok
        //strategy.setSuperServiceClass("com.jfeat.crud.CRUDServiceOnly");
        // ????????? service ???????????????
        // TODO, no default, check if ok
        //strategy.setSuperServiceImplClass("com.jfeat.crud.plus.impl.CRUDServiceOnlyImpl");
        // ????????? controller ??????
        //strategy.setSuperControllerClass("com.jfeat.am.common.controller.BaseController");

        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityColumnConstant(true);
        strategy.setEntityBuilderModel(true);
        mpg.setStrategy(strategy);

        // ?????????
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(this.parentPackage);
        packageConfig.setModuleName(this.moduleName);
        packageConfig.setEntity(this.entityPackage);
        packageConfig.setMapper(this.mapperPackage);
        packageConfig.setXml(this.xmlPackage);
        packageConfig.setService(this.servicePackage);
        packageConfig.setServiceImpl(this.serviceImplPackage);
        packageConfig.setController(this.controllerPackage);

        if (initialize) {
            // When initialize=true ???????????????service,
            // ???????????????persistence???service????????????

            packageConfig.setService("obsel.service");
            packageConfig.setServiceImpl("obsel.service.impl");
            packageConfig.setController("obsel.api");

            log.warn("??????????????????, ??????TemplateConfig setController/setService/setServiceImpl??????!");

            TemplateConfig tc = new TemplateConfig();
            tc.setController(null);
            tc.setService(null);
            tc.setServiceImpl(null);

            mpg.setTemplate(tc);

        }
        mpg.setPackageInfo(packageConfig);

        // ????????????????????????????????? VM ????????? cfg.abc ????????????
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("en", moduleName);
                map.put("am", parentModuleName);

                // ?????????????????????ID
                map.put("firstId", IdWorker.getId());
                map.put("secondId", IdWorker.getId());
                map.put("thirdId", IdWorker.getId());
                map.put("foreignId", IdWorker.getId());

                if (cruds != null) {

                    for (CRUDPojo pojo : cruds) {
                        log.info("???????????? CRDU " + pojo.toString());
                        if (pojo.getMask() == null) {
                            log.error("????????????CRUD??????????????????????????????");
                            throw new RuntimeException("????????????CRUD??????????????????????????????");
                        }

                        if (pojo.getMask().compareTo(CRUDPojo.MASTER) == 0) {
                            log.info("???CRUD?????????master???, master= " + pojo.getMaster());
                            map.put("master", pojo.getMaster());

                            // ?????????slave???masterId ???????????????????????????, ?????????Slave??????
                            /*map.put("masterId", pojo.getMasterId() == null || pojo.getMasterId().trim().length() == 0 ?
                                    pojo.getMaster() + "_id" :
                                    pojo.getMasterId().trim().contains(";") ? "" : pojo.getMasterId()
                            );*/

                            if (pojo.getSlaves() == null && pojo.getChildren() == null) {

                                log.info("???slaves???null??????master= serviceOnly");

                                map.put("master", "serviceOnly");
                                map.put("slaves", new ArrayList<String>());
                                map.put("children", new ArrayList<String>());

                            } else {
                                log.info("???CRUD?????????masterModel???, masterModel=" + pojo.getMaster());
                                map.put("masterModel", pojo.getMaster());

                                /// handle slaves
                                if (pojo.getSlaves() != null) {
                                    log.info("???slave??????null???, slaves=" + JSON.toJSON(pojo.getSlaves()));

                                    List<String> slaves = new ArrayList<>();

                                    // ?????? masterId
                                    if(pojo.getSlaves().size()==1 && pojo.getMasterId()!=null && pojo.getMasterId().trim().length()>0){
                                        String masterId = pojo.getMasterId();
                                        log.info(String.format("??????salve???masterId: %s", masterId));
                                        map.put("masterId", masterId);

                                        // check if one many by peer
                                        String ONE_MANY_PEER_SYMBOL = "::";
                                        if(masterId.contains(ONE_MANY_PEER_SYMBOL)){
                                            String peerRelationName = masterId.substring(0, masterId.indexOf(ONE_MANY_PEER_SYMBOL));
                                            log.info("??????salve???masterIdRelationName, relationName= " + getEntityName(peerRelationName, tablePrefix));
                                            map.put("masterIdRelationName", getEntityName(peerRelationName, tablePrefix));
                                        }

                                    }else if (pojo.getMasterId() != null && pojo.getMasterId().trim().length() > 0 && pojo.getMasterId().trim().contains(";")) {
                                        String[] ids = pojo.getMasterId().split(";");
                                        if (ids.length != pojo.getSlaves().size()) {
                                            log.error("?????????masterId??????????????????slaves???????????????slave?????????????????????masterId????????? " + pojo.getMasterId());
                                            throw new RuntimeException("masterId?????????slaves???????????????");
                                        }

                                        int no = 1;
                                        for (String masterId : ids) {

                                            String masterIdNo = "masterId" + no++;

                                            log.info(String.format("?????? ???%d???salve???masterId: %s", no, masterId));
                                            map.put(masterIdNo, masterId);
                                        }
                                    }

                                    /// ?????? slaves
                                    int masterIdCount=0; //??????Audit??????slave?????????masterId??????????????????slave??????????????????
                                    int no = 0;
                                    for (String pojoSlave : pojo.getSlaves()) {
                                        String slave = pojoSlave;

                                        String masterId = null;
                                        // just skip substring after masterId symbol :
                                        String MASTER_ID_SYMBOL = ":";
                                        if(pojoSlave.contains(MASTER_ID_SYMBOL)){
                                            masterId = pojoSlave.substring(pojoSlave.indexOf(MASTER_ID_SYMBOL)+1);
                                            slave = pojoSlave.substring(0,pojoSlave.indexOf(MASTER_ID_SYMBOL));
                                        }

                                        slaves.add(getEntityName(slave, tablePrefix));
                                        map.put("slave" + ++no, slave);

                                        if(masterId!=null) {
                                            masterIdCount++;
                                            map.put("masterId" + no, masterId);
                                        }
                                    }
                                    if(masterIdCount>0 && pojo.getSlaves()!=null && pojo.getSlaves().size()!=masterIdCount){
                                        log.error("?????????masterId????????????,masterId????????????slaves???????????????");
                                        throw new RuntimeException("?????????masterId????????????,masterId????????????slaves???????????????");
                                    }

                                    log.info("?????? cfg.slaves=" + JSON.toJSON(slaves));
                                    map.put("slaves", slaves);
                                }

                                // handle children,
                                // TODO,
                                if (pojo.getChildren() != null) {
                                    List<String> children = new ArrayList<>();
                                    for (String child : pojo.getChildren()) {
                                        children.add(getEntityName(child, tablePrefix));
                                    }

                                    log.info("?????? cfg.children=" + JSON.toJSON(children));
                                    map.put("children", children);

                                    int no = 1;
                                    for (String child : pojo.getChildren()) {
                                        map.put("child" + no++, child);
                                    }
                                }
                            }

                        } else if (pojo.getMask().compareTo(CRUDPojo.PEER) == 0) {
                            log.info("??????CRUD?????????peer???, more(master)=" + pojo.getMaster());
                            log.info("??????CRUD?????????peer???, moreTo(masterPeer)=" + pojo.getMasterPeer());
                            log.info("??????CRUD?????????peer???, relation=" + pojo.getRelation());
                            log.info("??????CRUD?????????peer???, moreName=" + getEntityName(pojo.getMaster(),tablePrefix));
                            log.info("??????CRUD?????????peer???, moreToName(moreTo)=" + getEntityName(pojo.getMasterPeer(),tablePrefix));
                            log.info("??????CRUD?????????peer???, relationName=" + getEntityName(pojo.getRelation(),tablePrefix));
                            map.put("more", pojo.getMaster());
                            map.put("moreTo", pojo.getMasterPeer());
                            map.put("relation", pojo.getRelation());
                            map.put("moreName", getEntityName(pojo.getMaster(), tablePrefix));
                            map.put("moreToName", getEntityName(pojo.getMasterPeer(), tablePrefix));
                            map.put("relationName", getEntityName(pojo.getRelation(), tablePrefix));

                        } else if (pojo.getMask().compareTo(CRUDPojo.GROUP) == 0) {
                            log.info("??????CRUD?????????group???, group= " + pojo.getGroup());
                            log.info("??????CRUD?????????group???, groupName= " + getEntityName(pojo.getGroup(), tablePrefix));
                            map.put("group", pojo.getGroup());
                            map.put("groupName", getEntityName(pojo.getGroup(),tablePrefix));

                            if (pojo.getGroupBy() != null && pojo.getGroupBy().length() > 0) {
                                log.info("??????CRUD?????????group???, groupBy= " + pojo.getGroupBy());
                                log.info("??????CRUD?????????group???, groupByName= " + getEntityName(pojo.getGroupBy(),tablePrefix));
                                log.info("??????CRUD?????????group???, groupId= " +  pojo.getGroupId());
                                map.put("groupBy", pojo.getGroupBy());
                                map.put("groupByName", getEntityName(pojo.getGroupBy(),tablePrefix));
                                map.put("groupId", pojo.getGroupId());
                            }

                        } else {
                            log.error("Invalid mask type: " + pojo.getMask());
                            throw new RuntimeException("Invalid mask type: " + pojo.getMask());
                        }
                    }

                } else if(cruds==null && includedTables != null) {
                    log.info("?????????cruds?????????,includedTables=" + JSON.toJSON(includedTables));
                } else {
                    log.error("?????????cruds=null?????????includedTables=null");
                    throw new RuntimeException("?????????cruds=null?????????includedTables=null");
                }

                String crudFilterPackagePath = getCRUDFilterPackagePath();
                String crudModelPackagePath = getCRUDModelPackagePath();
                String domainPackagePath = getDomainPackagePath();
                //String domainModelPackagePath = getDomainModelPackagePath();
                String domainRecordPackagePath = getDomainRecordPackagePath();
                //String serviceMapperDaoPath = getMapperPackagePath();

                map.put("filterPackage", crudFilterPackagePath.replace(File.separatorChar, '.')
                        .substring(crudFilterPackagePath.replace(File.separatorChar, '.').lastIndexOf("com"),
                                crudFilterPackagePath.length()));
                map.put("modelPackage", crudModelPackagePath.replace(File.separatorChar, '.')
                        .substring(crudModelPackagePath.replace(File.separatorChar, '.').lastIndexOf("com"),
                                crudModelPackagePath.length()));
                /*map.put("patchpackage", patchpackage.replace(File.separator, ".")
                        .substring(patchpackage.replace(File.separatorChar, '.')
                                .lastIndexOf("com"), patchpackage.replace(File.separator, ".").length()));*/
                map.put("domainPackage", domainPackagePath.replace(File.separatorChar, '.')
                        .substring(domainPackagePath.replace(File.separatorChar, '.').lastIndexOf("com"),
                                domainPackagePath.length()));
                /*map.put("domainModelPackage", domainModelPackagePath.replace(File.separatorChar, '.')
                        .substring(domainModelPackagePath.replace(File.separatorChar, '.').lastIndexOf("com"),
                                domainModelPackagePath.length()));*/
                map.put("domainRecordPackage", domainRecordPackagePath.replace(File.separatorChar, '.')
                        .substring(domainRecordPackagePath.replace(File.separatorChar, '.').lastIndexOf("com"),
                                domainRecordPackagePath.length()));
                // useless for mapperPackage
                //map.put("mapperPackage", serviceMapperDaoPath.replace(File.separatorChar, '.')
                //        .substring(serviceMapperDaoPath.replace(File.separatorChar, '.').lastIndexOf("com"),
                //                serviceMapperDaoPath.length()));

                this.setMap(map);
            }
        };

        if (!initialize) {
            List<FileOutConfig> focList = buildFileOutConfig(log);
            cfg.setFileOutConfigList(focList);
        }

        mpg.setCfg(cfg);

//
//        // ???????????? xml ????????????????????? ??? ?????????
        TemplateConfig tc = new TemplateConfig();
//        tc.setXml(null);
//        mpg.setTemplate(tc);

        // ?????????????????????????????? copy ?????? mybatis-plus/src/main/resources/templates ?????????????????????
        // ????????????????????? src/main/resources/templates ?????????, ??????????????????????????????????????????????????????????????????
        // TemplateConfig tc = new TemplateConfig();
        // tc.setController(null);
        //tc.setEntity(null);
        //tc.setMapper(null);
        //tc.setXml(null);
        //tc.setService(null);
        //tc.setServiceImpl(null);
        // ???????????????????????????????????? ??? OR Null ????????????????????????
        mpg.setTemplate(tc);

        log.info("AutoGenerator starts to execute ..");
        mpg.execute();
        log.info("AutoGenerator end executed!");

        // ?????????????????? persistence model ??????
        // com.jfeat.*.persistence.model.* ??????????????????
        boolean enableExtra = true;
        if(enableExtra) {
            String extraLine =
        "\n\n    @TableField(exist = false)\n" +
        "    private com.alibaba.fastjson.JSONObject extra;\n\n" +
        "    public com.alibaba.fastjson.JSONObject getExtra() {\n" +
        "        return extra;\n" +
        "    }\n" +
        "    public void setExtra(com.alibaba.fastjson.JSONObject extra) {\n" +
        "        this.extra = extra;\n" +
        "    }\n";

        //*ServiceImpl
        //@Override
        //protected String entityName() {
        //    return null;
        //}
            String flagLine = "private com.alibaba.fastjson.JSONObject extra;";

            File dir = new File(getEntityPackagePath());
            if (dir.exists()) {
                log.info("Add extra field in persistence models..");

                List<File> list = findFilesInDir(dir);

                for (File file : list) {
                    if (file.exists() && (!file.isDirectory())) {
                        String content = getFileContent(file);

                        if(!content.contains(flagLine)) {
                            content = content.replaceFirst("(\\{)", "$1" + extraLine);

                            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                            bufferedWriter.write(content);
                            bufferedWriter.close();

                            log.info("Add extra field in " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }

        /// remove obsel
        if (initialize) {
            File obsel = new File(getModulePackagePath() + File.separator + "obsel");

            if (obsel.exists()) {
                log.info("Try to remove obsel files in " + obsel.getAbsolutePath());

                List<File> list = findFilesInDir(obsel);
                for (File file : list) {
                    if (file.exists()) {
                        file.delete();

                        log.info("Removed obsel file " + file.getAbsolutePath());
                    }
                }

                // delete obsel file
                if (obsel.exists()) {
                    obsel.delete();
                    log.info("Removed obsel dir " + obsel.getAbsolutePath());
                }
            }
        }
    }

    private List<FileOutConfig> buildFileOutConfig(Log log) {
        boolean skip = false;

        // ?????? xml ??????????????????
        List<FileOutConfig> focList = new ArrayList<>();
        FileOutConfig fileOutConfig = null;
        final String NotFile = "//z//z";

        /*fileOutConfig = new FileOutConfig("/templates/Patch.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = outputDir + "/" + "com/jfeat/am/module/" + moduleName + "/api/patch" + "/PatchEndpoint" + ".java";
                if (!isCreate(f)) {
                    return NotFile;
                }
                return f;
            }
        };
        if(!skip) focList.add(fileOutConfig);*/

        fileOutConfig = new FileOutConfig("/templates/Filter.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getCRUDFilterPackagePath() + File.separator + tableInfo.getEntityName() + "Filter.java";
                if (!isCreate(f)) {
                    return NotFile;
                }

                // no Filter for group and groupby
                if (isGroupOrGroupBy(tableInfo.getName())) {
                    return NotFile;
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);

        /*fileOutConfig = new FileOutConfig("/templates/PatchService.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = patchPackage + "/PatchService.java";
                if (!isCreate(f)) {
                    return NotFile;
                }
                return f;
            }
        };
        if(!skip) focList.add(fileOutConfig);

        fileOutConfig = new FileOutConfig("/templates/PatchServiceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = patchPackage + "/impl/PatchServiceImpl.java";
                if (!isCreate(f)) {
                    return NotFile;
                }
                return f;
            }
        };
        if(!skip) focList.add(fileOutConfig);*/

        fileOutConfig = new FileOutConfig("/templates/serviceModel.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getCRUDModelPackagePath() + File.separatorChar + tableInfo.getEntityName() + "Model.java";
                if (!isCreate(f)) {
                    return NotFile;
                }

                /// master only
                if (!isMaster(tableInfo.getName())) {
                    return NotFile;
                }

                // not for group by
                if (isGroupBy(tableInfo.getName())) {
                    return NotFile;
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);


        /**
         * Domain Paths
         */
        /*fileOutConfig = new FileOutConfig("/templates/DomainModel.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getDomainModelPackagePath() + File.separatorChar + tableInfo.getEntityName() + "Model.java";

                if (!isCreate(f)) {
                    return NotFile;
                }

                /// master only
                if (!isMaster(tableInfo.getName())) {
                    return NotFile;
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);*/


        fileOutConfig = new FileOutConfig("/templates/DomainRecord.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getDomainRecordPackagePath() + File.separatorChar + tableInfo.getEntityName() + "Record.java";

                if (!isCreate(f)) {
                    return NotFile;
                }

                // no groupBy
                if (isGroupBy(tableInfo.getName())) {
                    return NotFile;
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);

        fileOutConfig = new FileOutConfig("/templates/DomainQueryDao.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getDomainPackagePath() + "/dao/mapping/" + "Query" + tableInfo.getEntityName() + "Dao.xml";
                if (!isCreate(f)) {
                    return NotFile;
                }

                // no for group by
                if (isGroupBy(tableInfo.getName())) {
                    return NotFile;
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);

        fileOutConfig = new FileOutConfig("/templates/DomainQueryDao.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getDomainPackagePath() + "/dao/" + "Query" + tableInfo.getEntityName() + "Dao.java";
                if (!isCreate(f)) {
                    return NotFile;
                }
                // no for group by
                if (isGroupBy(tableInfo.getName())) {
                    return NotFile;
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);

        fileOutConfig = new FileOutConfig("/templates/DomainService.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getDomainPackagePath() + "/service/" + tableInfo.getEntityName() + "Service.java";
                if (!isCreate(f)) {
                    return NotFile;
                }

                if (isMasterModel(tableInfo.getName())) {
                    f = f.replaceFirst("Service.java", "OverModelService.java");
                }

                if (isGroup(tableInfo.getName())) {
                    if(tableInfo.getName().endsWith("_category")){
                        // pass
                    }else if(tableInfo.getName().endsWith("_type")){
                        // pass
                    }else {
                        f = f.replaceFirst("Service.java", "GroupService.java");
                    }

                }else if(isGroupBy(tableInfo.getName())){
                    f = f.replaceFirst("Service.java", "GroupByService.java");
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);

        fileOutConfig = new FileOutConfig("/templates/DomainServiceImpl.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getDomainPackagePath() + "/service/impl/" + tableInfo.getEntityName() + "ServiceImpl.java";
                if (!isCreate(f)) {
                    return NotFile;
                }

                if (isMasterModel(tableInfo.getName())) {
                    f = f.replaceFirst("ServiceImpl.java", "OverModelServiceImpl.java");
                }

                if (isGroup(tableInfo.getName())) {
                    if(tableInfo.getName().endsWith("_category")){
                        // pass
                    }else if(tableInfo.getName().endsWith("_type")){
                        // pass
                    }else {
                        f = f.replaceFirst("ServiceImpl.java", "GroupServiceImpl.java");
                    }

                }else if(isGroupBy(tableInfo.getName())){
                    f = f.replaceFirst("ServiceImpl.java", "GroupByServiceImpl.java");
                }

                return f;
            }
        };
        if (!skip) focList.add(fileOutConfig);


        /**
         * Permission Paths
         */
        fileOutConfig = new FileOutConfig("/templates/Permission.java.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = getModulePackagePath() + "/api/permission/" + tableInfo.getEntityName() + "Permission.java";

                if (!isCreate(f)) {
                    return NotFile;
                }

                // only master
                if(!isMaster(tableInfo.getEntityName())){
                    return NotFile;
                }

                return f;
            }
        };
        if(!skip) focList.add(fileOutConfig);

        /*fileOutConfig = new FileOutConfig("/templates/Permission-data.sql.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                String f = "src/main/resources/sql/" + tableInfo.getEntityName() + "permission-data.sql";
                if (!isCreate(f)) {
                    return NotFile;
                }
                return f;
            }
        };
        if(!skip) focList.add(fileOutConfig);*/

        if (!skipTest) {
            fileOutConfig = new FileOutConfig("/templates/DemoTest.java.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    String f = getTestModulePackagePath() + "/ut/DemoTest.java";
                    if (!isCreate(f)) {
                        return NotFile;
                    }
                    return f;
                }
            };
            if (!skip) focList.add(fileOutConfig);
        }

        // add pom.xml
        if (!skipTest) {
            fileOutConfig = new FileOutConfig("/templates/pom.xml.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    String f = "pom.xml";
                    if (!isCreate(f)) {
                        return NotFile;
                    }
                    return f;
                }
            };
            if (!skip) focList.add(fileOutConfig);
        }
        return focList;
    }

    private static List<String> getEntityName(String[] tables) {
        List<String> list = new ArrayList<String>();
        for (String table : tables) {
            String temp = table.substring(table.lastIndexOf("_") + 1).toLowerCase();
            list.add(temp);
        }
        return list;
    }

    private static String getCamelCase(String origin) {
        String[] split = origin.split("_");
        String t = "";
        for (String temp : split) {
            t += temp.substring(0, 1).toUpperCase() + temp.substring(1).toLowerCase();
        }
        return t;
    }

    private static String getEntityName(String table, String[] tablePrefixes) {
        String tableWithoutPrefix = table;
        if(tablePrefixes!=null) {
            for (String prefix : tablePrefixes) {
                if (table.startsWith(prefix)){
                    tableWithoutPrefix = table.substring(prefix.length());
                    break;
                }
            }
        }

        return getCamelCase(tableWithoutPrefix);
    }

    private boolean isCreate(String filePath) {
        File file = new File(filePath);
        return !file.exists() || fileOverride;
    }


    /*
        ????????????Service???????????????????????????????????????Domain Service, ??????
     */
    @Deprecated
    private String getCRUDTypeName(String entityName) {
        if (cruds == null || cruds.length == 0) {
            return "";
        }

        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.MASTER) == 0) {
                if (pojo.getMaster().compareToIgnoreCase(entityName) == 0) {
                    return "";
                }

                // check slave
                if (pojo.getSlaves() != null && pojo.getSlaves().size() > 0) {
                    if (pojo.getSlaves() != null && pojo.getSlaves().size() > 0) {
                        for (String slave : pojo.getSlaves()) {
                            if (slave.compareToIgnoreCase(entityName) == 0) {
                                return "Slave";
                            }
                        }
                    }
                }
            }

            // check group
            if (pojo.getMask().compareTo(CRUDPojo.GROUP) == 0) {
                if (pojo.getGroup() != null && pojo.getGroup().compareTo(entityName) == 0) {
                    return "";
                }
                if (pojo.getGroupBy() != null && pojo.getGroupBy().compareTo(entityName) == 0) {
                    return "GroupBy";
                }
            }

        }// end for
        return "";
    }

    /**
     * ??????????????? master,???????????? master????????? slaves
     *
     * @param entityName
     * @return
     */
    private boolean isMaster(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.MASTER) == 0) {
                if (pojo.getMaster().compareToIgnoreCase(entityName) == 0) {
                    /*if(pojo.getSlaves()!=null && pojo.getSlaves().size()>0){
                        return true;
                    }
                    if(pojo.getChildren()!=null && pojo.getChildren().size()>0){
                        return true;
                    }*/
                    return true;
                }
            }
        }

        return false;
    }
    private boolean isMasterModel(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.MASTER) == 0) {
                if (pojo.getMaster().compareToIgnoreCase(entityName) == 0) {
                    if(pojo.getSlaves()!=null && pojo.getSlaves().size()>0){
                        return true;
                    }
                    if(pojo.getChildren()!=null && pojo.getChildren().size()>0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSlave(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }

        // let's check
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.MASTER) == 0) {
                if (pojo.getSlaves() != null && pojo.getSlaves().size() > 0) {
                    for (String slave : pojo.getSlaves()) {
                        if (slave.compareToIgnoreCase(entityName) == 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isMasterOrSlave(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }

        // let's check
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.MASTER) == 0) {
                if (pojo.getMaster().compareToIgnoreCase(entityName) == 0) {
                    return true;
                }

                // check slave
                if (pojo.getSlaves() != null && pojo.getSlaves().size() > 0) {
                    for (String slave : pojo.getSlaves()) {
                        if (slave.compareToIgnoreCase(entityName) == 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isGroup(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.GROUP) == 0) {
                if ( pojo.getGroup() != null && pojo.getGroup().compareTo(entityName) == 0) {
                    if(pojo.getGroupBy()==null || pojo.getGroupBy().length()==0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isGroupBy(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.GROUP) == 0) {
                if (pojo.getGroupBy() != null && pojo.getGroupBy().compareTo(entityName) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isGroupOrGroupBy(String entityName) {
        /// just ignore check, if cruds are not config
        if (cruds == null || cruds.length == 0) {
            return false;
        }
        for (CRUDPojo pojo : cruds) {
            if (pojo.getMask().compareTo(CRUDPojo.GROUP) == 0) {
                if (pojo.getGroup() != null && pojo.getGroup().compareTo(entityName) == 0) {
                    return true;
                }
                if (pojo.getGroupBy() != null && pojo.getGroupBy().compareTo(entityName) == 0) {
                    return true;
                }
            }
        }
        return false;
    }


    public static ArrayList<String> splitCamelCaseString(String s) {
        ArrayList<String> result = new ArrayList<String>();
        for (String w : s.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
            result.add(w);
        }
        return result;
    }

    private static List<File> findFilesInDir(File dir) {
        List<File> list = new ArrayList<File>();
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("??????????????????" + dir.getAbsolutePath());
        }
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile())
                list.add(file);
            else
                list.addAll(findFilesInDir(file));
        }

        list.add(dir);
        return list;
    }

    public static String[] getTablePrefix(String databaseUrl, String username, String password, String driverClass) {
        Map<String, String> tablenames = new HashMap<String, String>();

        try {
            Class.forName(driverClass);
            Connection connection = (username != null && username.length() > 0)
                    ? DriverManager.getConnection(databaseUrl, username, password)
                    : DriverManager.getConnection(databaseUrl);

            String sql = "SHOW TABLES";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String tablename = rs.getString(1);

                if (tablename.contains("_")) {
                    int index = tablename.indexOf("_") + 1;
                    String temp = tablename.substring(0, index);

                    if (!tablenames.containsKey(temp)) {
                        tablenames.put(temp, temp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Object[] values = tablenames.values().toArray();

        /// only prefix.length <=4  e.g."wms_"
        List<String> prefixList = new ArrayList<>();
        //String[] tableprefixs = new String[tablenames.size()];
        for (int i = 0; i < tablenames.values().size(); i++) {
            //tableprefixs[i] = values[i].toString();
            if (values[i].toString().length() <= 4) {
                prefixList.add(values[i].toString());
            }
        }

        String[] tableprefixs = new String[prefixList.size()];
        for (int i = 0; i < prefixList.size(); i++) {
            tableprefixs[i] = prefixList.get(i);
        }
        return tableprefixs;
    }

    private static List<String> SqlFilter(Log log, File file) {

        List<String> sqlList = new ArrayList<String>();

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                if (line == null || line.length() == 0) continue;

                /// check comment line
                line = line.replaceAll("^\\s*", "");
                if (line.startsWith("-")) continue;

                sqlList.add(line);
            }

            StringBuilder lines = new StringBuilder();
            for (String l : sqlList) {
                lines.append(l);
            }
            sqlList.clear();
            line = lines.toString();

            while (line.contains("/*")) {
                String s = line.substring(line.indexOf("/*"), line.indexOf("*/") + 2);
                line = line.replace(s, "");
            }

            return Arrays.asList(line.split(";"));

        } catch (FileNotFoundException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private static void sqlToDataBase(Log log, String databaseUrl, String username, String password, String driverClass) throws Exception {
        File fileDir = new File("src/main/resources/sql");
        if (!fileDir.exists()) throw new IllegalArgumentException("??????????????????");
        File[] files = fileDir.listFiles();

        /// filter schema sql files
        List<File> sqlFiles = new ArrayList<>();

        for (File f : files) {
            if (f.getName().toLowerCase().contains("-schema.sql")) {
                sqlFiles.add(f);
            }
        }
        List<String> sql = new ArrayList<>();
        for (File sqlFile : sqlFiles) {
            sql.addAll(SqlFilter(log, sqlFile));
        }

        executeSqlMessage(sql, databaseUrl, username, password, driverClass);
    }

    private static void executeSqlMessage(List<String> sqls, String databaseUrl, String username, String password, String driverClass) throws Exception {
        Class.forName(driverClass);

        Connection connection = (username != null && username.length() > 0)
                ? DriverManager.getConnection(databaseUrl, username, password)
                : DriverManager.getConnection(databaseUrl);
        PreparedStatement preparedStatement = null;
        connection.setAutoCommit(false);
        preparedStatement = connection.prepareStatement("");
        for (String s : sqls) {

            preparedStatement.addBatch(s);

        }
        preparedStatement.executeBatch();
        connection.commit();
    }


    /**
     * Deprecated
     *
     * @throws IOException
     */

    /*@Deprecated
    public static void FileMakeUp(String dir) throws IOException {
        File out = new File("src/main/resources/sql/permission-data.sql");
        if (out.exists()) {
            out.delete();
        } else {
            out.createNewFile();
        }
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(out));
        File file = new File(dir);
        if (!file.exists()) {
            throw new IllegalArgumentException("??????????????????");
        } else {
            if (!file.isDirectory()) {
                throw new IllegalArgumentException("???????????????");
            } else {
                File[] files = file.listFiles();
                List<String> list = new ArrayList<String>();
                for (File f : files) {
                    if (!f.getName().toLowerCase().contains("-schema.sql")) {
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            list.add(line);
                        }
                        bufferedReader.close();
                        f.delete();
                    }
                }
                for (String ll : list) {
                    bufferedWriter.write(ll);
                    bufferedWriter.flush();
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
            }
        }
    }*/
    @Deprecated
    public static void chooseTables(String module, String modulePackagePath, String[] tables) {
        File dir = new File(modulePackagePath + File.separator + module);

        List<File> files = findFilesInDir(dir);
        List<File> fs = new ArrayList<File>();

        if (tables != null) {
            List<String> tablenames = new ArrayList<String>();
            for (String table : tables) {
                if (table.contains("_")) {
                    int first = table.indexOf('_') + 1;
                    table = table.substring(first);
                    //int num = table.split("_").length;
                    //table = table.split("_")[num-1];
                }
                tablenames.add(table);
            }

            for (File file : files) {
                for (String tablename : tablenames) {
                    tablename = tablename.replace("_", "");
                    if (file.getName().toLowerCase().contains(tablename) || file.getAbsolutePath().contains("patch")) {
                        fs.add(file);
                    }
                }
            }

        } else {
            ///??????????????????tables, ????????????
            for (File file : files) {
                fs.add(file);
            }
        }

        ///????????????
        for (File f : fs) {
            f.setReadOnly();
        }

        /// ??????????????????
        for (File f : files) {
            if (f.canWrite())
                f.delete();
        }

        /// ????????????
        for (File f : fs) {
            f.setWritable(true);
        }
    }

    @Deprecated
    private static void addDependence(File target) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(target);
        List<Element> elements = document.getRootElement().elements();
        for (Element element : elements) {
            if (element.getName().toLowerCase().equalsIgnoreCase("dependencies")) {
                if (!(element.asXML().contains("sb-crud") || element.asXML().contains("${sb-crud.version}"))) {
                    Element e = element.addElement("dependency");
                    e.addElement("groupId").setText("com.jfeat");
                    e.addElement("artifactId").setText("sb-crud");
                    e.addElement("version").setText("${sb-crud.version}");
                }
            }
            if (element.getName().toLowerCase().equalsIgnoreCase("dependencies")) {
                if (!(element.asXML().contains("${sb-base.version}") || element.asXML().contains("sb-base"))) {
                    Element e = element.addElement("dependency");
                    e.addElement("groupId").setText("com.jfeat");
                    e.addElement("artifactId").setText("sb-base");
                    e.addElement("version").setText("${sb-base.version}");
                }
            }
            if (element.getName().toLowerCase().equalsIgnoreCase("properties")) {
                if (!element.asXML().contains("sb-base.version")) {
                    Element e = element.addElement("sb-base.version");
                    e.addText("1.0.0");
                }
            }
            if (element.getName().toLowerCase().equalsIgnoreCase("properties")) {
                if (!element.asXML().contains("sb-crud.version")) {
                    Element e = element.addElement("sb-crud.version");
                    e.addText("1.0.3-SNAPSHOT");
                }
            }
            if (element.getName().toLowerCase().equalsIgnoreCase("dependencies")) {
                if (!element.asXML().contains("spring-test")) {
                    Element e = element.addElement("dependency");
                    e.addElement("groupId").setText("org.springframework");
                    e.addElement("artifactId").setText("spring-test");
                    e.addElement("version").setText("4.3.11.RELEASE");
                }
            }
            if (element.getName().toLowerCase().equalsIgnoreCase("dependencies")) {
                if (!element.asXML().contains("spring-boot-test")) {
                    Element e = element.addElement("dependency");
                    e.addElement("groupId").setText("org.springframework.boot");
                    e.addElement("artifactId").setText("spring-boot-test");
                    e.addElement("version").setText("1.5.7.RELEASE");
                }
            }
        }
        OutputFormat outputFormat = new OutputFormat();
        outputFormat.setEncoding("utf-8");
        XMLWriter xmlWriter = new XMLWriter(new FileWriter(target), outputFormat);
        xmlWriter.write(document);
        xmlWriter.close();
        String s = getFileContent(target);
        //System.out.printf("<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">"+s);
        String PoxMessage = format(s);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(target));

        bufferedWriter.write(PoxMessage);

        bufferedWriter.close();
    }

    @Deprecated
    private static String format(String str) throws Exception {
        SAXReader reader = new SAXReader();
        // System.out.println(reader);
        // ??????????????????????????????????????????
        StringReader in = new StringReader(str);
        Document doc = reader.read(in);
        // System.out.println(doc.getRootElement());
        // ???????????????????????????
        OutputFormat formater = OutputFormat.createPrettyPrint();
        //formater=OutputFormat.createCompactFormat();
        // ???????????????xml???????????????
        formater.setEncoding("utf-8");
        // ?????????????????????(??????)
        StringWriter out = new StringWriter();
        // ????????????????????????
        XMLWriter writer = new XMLWriter(out, formater);
        // ????????????????????????????????????????????????????????????????????????????????????out??????
        writer.write(doc);

        writer.close();
        //System.out.println(out.toString());
        // ??????????????????????????????????????????
        return out.toString();
    }

    @Deprecated
    private static void updateTables(String[] updateTables, String outputDir, String moduleName, boolean updateOverall) throws IOException {
        if (updateTables != null && updateTables.length != 0) {

            File dir = new File(new StringBuffer().append(outputDir)
                    .append(File.separator).append(moduleName).toString());

            File newDir = new File(new StringBuffer(outputDir).append(File.separator) + "temp");
            newDir.mkdirs();
            List<File> files = findFilesInDir(dir);
            List<String> entityname = getEntityName(updateTables);
            for (File file : files) {
                for (String s : entityname) {
                    if (file.getName().toLowerCase().contains(s)) {
                        if (updateOverall) {
                            file.delete();
                        } else if (file.getAbsolutePath().contains("persistence")) {
                            /// only update of persistence
                            file.delete();
                        }
                    }
                }
            }
            moveDir(dir, newDir);
            dir.delete();
        }
    }

    @Deprecated
    private static void moveDir(File sourceDir, File targetDir) throws IOException {
        List<File> files = findFilesInDir(sourceDir);
        for (File file : files) {
            String filePath = file.getAbsolutePath().replace(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());
            File f = new File(filePath);
            moveFiles(file, f);
        }
    }

    @Deprecated
    private static void moveFiles(File source, File target) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(source);

        File parentFile = target.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(target);
        byte[] bs = new byte[3 * 1024 * 1024];
        int b;
        while ((b = fileInputStream.read(bs, 0, bs.length)) != -1) {
            fileOutputStream.write(bs, 0, b);
        }
        fileOutputStream.close();
        fileInputStream.close();
    }

    @Deprecated
    private static void deleteTemp(File dir) {
        List<File> fileList = findFilesInDir(dir);
        for (File file : fileList) {
            file.delete();
        }
        List<File> allDir = findAllDir(dir);
        for (File file : allDir) {
            file.delete();
        }
    }

    @Deprecated
    private static List<File> findAllDir(File dir) {
        List<File> list = new ArrayList<File>();
        list.add(dir);
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                list.addAll(findAllDir(file));
            }
        }
        return list;
    }

    private static String getFileContent(File file) throws IOException {

        final String NEWLINE = "\r\n";
        StringBuilder builder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
            builder.append(NEWLINE);
        }
        bufferedReader.close();

        return builder.toString();
    }
}

