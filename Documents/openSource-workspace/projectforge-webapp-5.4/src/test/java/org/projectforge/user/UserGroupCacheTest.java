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

package org.projectforge.user;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.projectforge.test.TestBase;

public class UserGroupCacheTest extends TestBase
{
  private GroupDao groupDao;

  private UserGroupCache userGroupCache;

  public void setGroupDao(final GroupDao groupDao)
  {
    this.groupDao = groupDao;
  }

  /**
   * @param userGroupCache the userGroupCache to set
   * @return this for chaining.
   */
  public void setUserGroupCache(final UserGroupCache userGroupCache)
  {
    this.userGroupCache = userGroupCache;
  }

  @Test
  public void testUserMemberOfAtLeastOneGroup()
  {
    logon(TEST_ADMIN_USER);
    GroupDO group1 = new GroupDO();
    group1.setName("testusergroupcache1");
    Set<PFUserDO> assignedUsers = new HashSet<PFUserDO>();
    group1.setAssignedUsers(assignedUsers);
    assignedUsers.add(getUser(TEST_USER));
    Serializable id = groupDao.save(group1);
    group1 = groupDao.getById(id);

    GroupDO group2 = new GroupDO();
    group2.setName("testusergroupcache2");
    assignedUsers = new HashSet<PFUserDO>();
    group2.setAssignedUsers(assignedUsers);
    assignedUsers.add(getUser(TEST_ADMIN_USER));
    id = groupDao.save(group2);
    group2 = groupDao.getById(id);

    assertFalse(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId()));
    assertFalse(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId(), group1.getId()));
    assertTrue(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId(), group2.getId()));
    assertTrue(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId(), group1.getId(), group2.getId()));
    assertTrue(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId(), null, group1.getId(), group2.getId()));
    assertTrue(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId(), null, group1.getId(), null, group2.getId(), null));
    assertTrue(userGroupCache.isUserMemberOfAtLeastOneGroup(getUser(TEST_ADMIN_USER).getId(), group2.getId(), group1.getId()));
  }
}
