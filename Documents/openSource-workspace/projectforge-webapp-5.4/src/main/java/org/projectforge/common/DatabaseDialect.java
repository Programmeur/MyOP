/////////////////////////////////////////////////////////////////////////////
//
// Project   ProjectForge
//
// Copyright 2001-2009, Micromata GmbH, Kai Reinhard
//           All rights reserved.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.common;

/**
 * @author Di (609366205@qq.com)
 *
 */
public enum DatabaseDialect
{
	MYSQL("org.hibernate.dialect.MySQLDialect"), PostgreSQL("org.hibernate.dialect.PostgreSQLDialect"), HSQL("org.hibernate.dialect.HSQLDialect");
	// Not yet supported:
	// MYSQL, ORACLE, MS_SQL_SERVER, DB2, INFORMIX, DERBY, UNKOWN;

	private String asString;

	public static DatabaseDialect fromString(final String asString)
	{
		if (MYSQL.asString.equals(asString) == true) {
			return MYSQL;
		}
		if (PostgreSQL.asString.equals(asString) == true) {
			return PostgreSQL;
		}
		if (HSQL.asString.equals(asString) == true) {
			return HSQL;
		}
		return null;
	}

	/**
	 * @return the asString
	 */
	public String getAsString()
	{
		return asString;
	}

	private DatabaseDialect(final String asString)
	{
		this.asString = asString;
	}
}
