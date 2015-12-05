package com.ctg.itrdc.code.bean;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctg.itrdc.code.util.Constants;
import com.ctg.itrdc.code.util.JavaBeanUtil;
import com.ctg.itrdc.code.util.JdbcUtils;
import com.ctg.itrdc.code.util.PropertiesUtil;

public class MetaDataBean {

	private String tableName;

	private String className;
	
	private String classAttrName;

	private String entityName;

	private String basePackage;

	private AttrInfo pkAttrInfo;

	private List<AttrInfo> attrInfos = new ArrayList<AttrInfo>();

	private Map<String, AttrInfo> attrMap = new HashMap<String, AttrInfo>();

	public String getTableName() {
		return tableName.toUpperCase();
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getClassName() {
		return JavaBeanUtil.toCamelCaseString(className, true);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getEntityName() {
		return JavaBeanUtil.toCamelCaseString(entityName, false);
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
	public String getClassAttrName() {
        return classAttrName;
    }

    public void setClassAttrName(String classAttrName) {
        this.classAttrName = classAttrName;
    }

    public AttrInfo getPkAttrInfo() {
		return pkAttrInfo;
	}

	public void setPkAttrInfo(AttrInfo pkAttrInfo) {
		this.pkAttrInfo = pkAttrInfo;
	}

	public List<AttrInfo> getAttrInfos() {
		return attrInfos;
	}

	public void setAttrInfos(List<AttrInfo> attrInfos) {
		this.attrInfos = attrInfos;
	}

	public MetaDataBean(String tableName, String basePackage) {
		 this.tableName = tableName;
	        this.basePackage = basePackage;
	        tableName = tableName.toUpperCase();
	        Pattern pattern = Pattern.compile("^[A-Z]{2}\\_");
	        Matcher matcher = pattern.matcher(tableName);
	        boolean find = false;
	        while (matcher.find()) {
	            find = true;
	            String temp = tableName.substring(matcher.start(), matcher.end());
	            tableName = tableName.replaceAll(temp, "");
	            matcher = pattern.matcher(tableName);
	        }
	        
	        if (!find) {
	            String prefixTableNames = PropertiesUtil.getConfigValueCache(Constants.CONFIG_FILE,
	                Constants.FTABLENAME);
	            for (String s : prefixTableNames.toUpperCase().split(",")) {
	                if (tableName.startsWith(s)) {
	                    tableName = tableName.replace(s, "");
	                    break;
	                }
	            }
	        }
	        this.setEntityName(tableName);
	        this.setClassName(tableName);
	        this.setClassAttrName(JavaBeanUtil.getValidPropertyName(this.getClassName()));
	        createMetaInfo();
	}

	public void createMetaInfo() {
		String sql = "select * from " + tableName;
		Connection conn = null; // 得到数据库连接
		try {
			conn = JdbcUtils.getInstance().getServerConnection();
			DatabaseMetaData dmd = conn.getMetaData();
			ResultSet set2 = dmd.getColumns(null, null, tableName, null);
			while (set2.next()) {
				AttrInfo info = new AttrInfo();
				info.setColumnName(set2.getString("COLUMN_NAME").toUpperCase());
				info.setDbType(set2.getString("TYPE_NAME"));
				info.setComment(set2.getString("REMARKS")!=null?set2.getString("REMARKS"):"");
				attrMap.put(info.getColumnName(), info);
			}
			// PreparedStatement pstmt = conn.prepareStatement(sql);
			// ResultSetMetaData rsmd = pstmt.getMetaData();
			// int size = rsmd.getColumnCount(); // 共有多少列
			// for (int i = 0; i < size; i++) {
			// // colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
			// AttrInfo info = new AttrInfo();
			// info.setColumnName(rsmd.getColumnName(i + 1));
			// info.setDbType(rsmd.getColumnTypeName(i + 1));
			// attrMap.put(info.getColumnName(), info);
			// }

			ResultSet set = conn.getMetaData().getPrimaryKeys(null, null,
					tableName);
			if (set.next()) {
				String column = set.getString("COLUMN_NAME").toUpperCase();
				pkAttrInfo = attrMap.remove(column);
			}

			attrInfos = new ArrayList<AttrInfo>(attrMap.values());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.free(conn, null, null);
		}
	}

	public static void main(String[] args) {
		MetaDataBean bean = new MetaDataBean("PZ_GG_SERVICE_OFFER",
				"com.ctg.itrdc.product");
		bean.createMetaInfo();
		System.out.println(bean.getClassName());
		System.out.println(bean.getPkAttrInfo());
		System.out.println(bean.getAttrInfos());
		System.out.println(bean.getClassAttrName());
	}

}
