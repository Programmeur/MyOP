/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2014 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.web.wicket.components;

import junit.framework.Assert;

import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.junit.Test;
import org.projectforge.core.BaseSearchFilter;
import org.projectforge.test.TestBase;
import org.projectforge.user.PFUserDO;

public class MaxLenfthTextFieldTest extends TestBase
{
  @Test
  public void maxLength()
  {
    PropertyModel<String> model = new PropertyModel<String>(new PFUserDO(), "username");
    assertInteger(255, MaxLengthTextField.getMaxLength(model));
    assertInteger(255, MaxLengthTextField.getMaxLength(model, 300));
    assertInteger(100, MaxLengthTextField.getMaxLength(model, 100));

    model = new PropertyModel<String>(new BaseSearchFilter(), "searchString");
    Assert.assertNull(MaxLengthTextField.getMaxLength(model));
    assertInteger(100, MaxLengthTextField.getMaxLength(model, 100));

    Assert.assertNull(MaxLengthTextField.getMaxLength(Model.of("test")));
    assertInteger(100, MaxLengthTextField.getMaxLength(Model.of("test"), 100));
  }

  private void assertInteger(final int expectedValue, final Integer value)
  {
    Assert.assertNotNull(value);
    Assert.assertEquals(expectedValue, value.intValue());
  }
}
