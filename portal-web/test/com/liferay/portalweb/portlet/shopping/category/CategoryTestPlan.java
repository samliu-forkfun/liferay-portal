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

package com.liferay.portalweb.portlet.shopping.category;

import com.liferay.portalweb.portal.BaseTestSuite;
import com.liferay.portalweb.portlet.shopping.category.addcategory.AddCategoryTests;
import com.liferay.portalweb.portlet.shopping.category.addcategorymultiple.AddCategoryMultipleTests;
import com.liferay.portalweb.portlet.shopping.category.addcategorynamedoublebackslash.AddCategoryNameDoubleBackSlashTests;
import com.liferay.portalweb.portlet.shopping.category.addcategorynamedoubleforwardslash.AddCategoryNameDoubleForwardSlashTests;
import com.liferay.portalweb.portlet.shopping.category.addcategorynamenull.AddCategoryNameNullTests;
import com.liferay.portalweb.portlet.shopping.category.addsubcategory.AddSubcategoryTests;
import com.liferay.portalweb.portlet.shopping.category.addsubcategorymultiple.AddSubcategoryMultipleTests;
import com.liferay.portalweb.portlet.shopping.category.deletecategory.DeleteCategoryTests;
import com.liferay.portalweb.portlet.shopping.category.editcategory.EditCategoryTests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Brian Wing Shun Chan
 */
public class CategoryTestPlan extends BaseTestSuite {

	public static Test suite() {
		TestSuite testSuite = new TestSuite();

		testSuite.addTest(AddCategoryTests.suite());
		testSuite.addTest(AddCategoryMultipleTests.suite());
		testSuite.addTest(AddCategoryNameDoubleBackSlashTests.suite());
		testSuite.addTest(AddCategoryNameDoubleForwardSlashTests.suite());
		testSuite.addTest(AddCategoryNameNullTests.suite());
		testSuite.addTest(AddSubcategoryTests.suite());
		testSuite.addTest(AddSubcategoryMultipleTests.suite());
		testSuite.addTest(DeleteCategoryTests.suite());
		testSuite.addTest(EditCategoryTests.suite());

		return testSuite;
	}

}