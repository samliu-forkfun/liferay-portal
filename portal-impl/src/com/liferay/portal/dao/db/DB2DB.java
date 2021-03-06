/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.dao.db;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexander Chow
 * @author Bruno Farache
 * @author Sandeep Soni
 * @author Ganesh Ram
 */
public class DB2DB extends BaseDB {

	public static DB getInstance() {
		return _instance;
	}

	@Override
	public String buildSQL(String template) throws IOException {
		template = convertTimestamp(template);
		template = replaceTemplate(template, getTemplate());

		template = reword(template);
		template = removeLongInserts(template);
		template = removeNull(template);
		template = StringUtil.replace(template, "\\'", "''");

		return template;
	}

	@Override
	public boolean isSupportsAlterColumnType() {
		return _SUPPORTS_ALTER_COLUMN_TYPE;
	}

	@Override
	public boolean isSupportsInlineDistinct() {
		return _SUPPORTS_INLINE_DISTINCT;
	}

	@Override
	public boolean isSupportsScrollableResults() {
		return _SUPPORTS_SCROLLABLE_RESULTS;
	}

	@Override
	public void runSQL(String template) throws IOException, SQLException {
		if (template.startsWith(ALTER_COLUMN_NAME)) {
			String sql = buildSQL(template);

			String[] alterSqls = StringUtil.split(sql, CharPool.SEMICOLON);

			for (String alterSql : alterSqls) {
				runSQL(alterSql);
			}
		}
		else {
			super.runSQL(template);
		}
	}

	@Override
	public void runSQL(String[] templates) throws IOException, SQLException {
		super.runSQL(templates);

		_reorgTables(templates);
	}

	protected DB2DB() {
		super(TYPE_DB2);
	}

	@Override
	protected String buildCreateFileContent(
			String sqlDir, String databaseName, int population)
		throws IOException {

		String suffix = getSuffix(population);

		StringBundler sb = new StringBundler(14);

		sb.append("drop database ");
		sb.append(databaseName);
		sb.append(";\n");
		sb.append("create database ");
		sb.append(databaseName);
		sb.append(" pagesize 8192;\n");
		sb.append("connect to ");
		sb.append(databaseName);
		sb.append(";\n");
		sb.append(
			readFile(
				sqlDir + "/portal" + suffix + "/portal" + suffix + "-db2.sql"));
		sb.append("\n\n");
		sb.append(readFile(sqlDir + "/indexes/indexes-db2.sql"));
		sb.append("\n\n");
		sb.append(readFile(sqlDir + "/sequences/sequences-db2.sql"));

		return sb.toString();
	}

	@Override
	protected String getServerName() {
		return "db2";
	}

	@Override
	protected String[] getTemplate() {
		return _DB2;
	}

	@Override
	protected String reword(String data) throws IOException {
		UnsyncBufferedReader unsyncBufferedReader = new UnsyncBufferedReader(
			new UnsyncStringReader(data));

		StringBundler sb = new StringBundler();

		String line = null;

		while ((line = unsyncBufferedReader.readLine()) != null) {
			if (line.startsWith(ALTER_COLUMN_NAME)) {
				String[] template = buildColumnNameTokens(line);

				line = StringUtil.replace(
					"alter table @table@ add column @new-column@ @type@;\n",
					REWORD_TEMPLATE, template);

				line = line + StringUtil.replace(
					"update @table@ set @new-column@ = @old-column@;\n",
					REWORD_TEMPLATE, template);

				line = line + StringUtil.replace(
					"alter table @table@ drop column @old-column@",
					REWORD_TEMPLATE, template);
			}
			else if (line.indexOf(DROP_INDEX) != -1) {
				String[] tokens = StringUtil.split(line, ' ');

				line = StringUtil.replace(
					"drop index @index@;", "@index@", tokens[2]);
			}

			sb.append(line);
			sb.append("\n");
		}

		unsyncBufferedReader.close();

		return sb.toString();
	}

	private void _reorgTables(String[] templates) throws SQLException {
		Set<String> tableNames = new HashSet<String>();

		for (String template : templates) {
			if (template.startsWith("alter table")) {
				tableNames.add(template.split(" ")[2]);
			}
		}

		if (tableNames.size() == 0) {
			return;
		}

		Connection con = null;
		CallableStatement callStmt = null;

		try {
			con = DataAccess.getConnection();

			for (String tableName : tableNames) {
				String sql = "call sysproc.admin_cmd(?)";

				callStmt = con.prepareCall(sql);

				String param = "reorg table " + tableName;

				callStmt.setString(1, param);

				callStmt.execute();
			}
		}
		finally {
			DataAccess.cleanUp(con, callStmt);
		}
	}

	private static final String[] _DB2 = {
		"--", "1", "0", "'1970-01-01-00.00.00.000000'", "current timestamp",
		" blob", " blob", " smallint", " timestamp", " double", " integer",
		" bigint", " varchar(500)", " clob", " varchar",
		" generated always as identity", "commit"
	};

	private static final boolean _SUPPORTS_ALTER_COLUMN_TYPE = false;

	private static final boolean _SUPPORTS_INLINE_DISTINCT = false;

	private static final boolean _SUPPORTS_SCROLLABLE_RESULTS = false;

	private static DB2DB _instance = new DB2DB();

}