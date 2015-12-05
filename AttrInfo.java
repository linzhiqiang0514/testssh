package com.ctg.itrdc.code.bean;

import com.ctg.itrdc.code.util.JavaBeanUtil;

public class AttrInfo {
	private String columnName;

	private String dbType;

	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getColumnName() {
		return columnName.toUpperCase();
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getFiledName() {
		return JavaBeanUtil.toCamelCaseString(this.getColumnName(), false);
	}

	public String getJavaType() {
		return sqlType2JavaType(dbType);
	}

	public String getSetMethod() {
		return "set"
				+ JavaBeanUtil.toCamelCaseString(this.getColumnName(), true);
	}

	public String getGetMeth() {
		return "get"
				+ JavaBeanUtil.toCamelCaseString(this.getColumnName(), true);
	}

	private String sqlType2JavaType(String sqlType) {
		if (sqlType.equalsIgnoreCase("bit")) {
			return "bool";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "byte";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "int";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "Long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal")) {
			return "Long";
		} else if (sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar")
				|| sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar")
				|| sqlType.equalsIgnoreCase("nchar")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("DATE")
				|| sqlType.equalsIgnoreCase("year")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("TIMESTAMP")
				|| sqlType.equalsIgnoreCase("DATETIME")) {
			return "Timestamp";
		}

		else if (sqlType.equalsIgnoreCase("image")) {
			return "Blob";
		} else if (sqlType.equalsIgnoreCase("text")) {
			return "Clob";
		}
		return null;
	}

	@Override
	public String toString() {
		return "AttrInfo [columnName=" + columnName + ", dbType=" + dbType
				+ ", getColumnName()=" + getColumnName() + ", getDbType()="
				+ getDbType() + ", getFiledName()=" + getFiledName()
				+ ", getJavaType()=" + getJavaType() + ", getSetMethod()="
				+ getSetMethod() + ", getGetMeth()=" + getGetMeth() +", getComment=" + getComment() + "]";
	}

}