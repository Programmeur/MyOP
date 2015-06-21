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

package org.projectforge.plugins.skillmatrix;

import java.io.IOException;

import org.projectforge.plugins.teamcal.TeamCalPlugin;
import org.projectforge.test.AbstractTestBase;
import org.projectforge.test.PluginTestBase;
import org.projectforge.user.GroupDO;
import org.projectforge.user.PFUserDO;
import org.springframework.beans.BeansException;

/**
 * @author Werner Feder (werner.feder@t-online.de)
 *
 */
public class SkillTestHelper
{
  private PFUserDO owner, fullUser1, fullUser2, fullUser3, readonlyUser1, readonlyUser2, readonlyUser3, trainingUser1, trainingUser2,
  trainingUser3, noAccessUser;

  private GroupDO fullGroup1, readonlyGroup1, trainingGroup1, fullGroup2, readonlyGroup2, trainingGroup2;

  public static void setup() throws BeansException, IOException
  {
    setup(true);
  }

  public static void setup(final boolean createTestData) throws BeansException, IOException
  {
    final String [] contextFiles = { "org/projectforge/plugins/teamcal/pluginContext.xml", "org/projectforge/plugins/skillmatrix/pluginContext.xml"};
    PluginTestBase.init(contextFiles, createTestData,  new TeamCalPlugin() ,new SkillMatrixPlugin());
  }

  public SkillDO prepareUsersAndGroups(final String prefix, final AbstractTestBase testBase, final SkillDao skillDao)
  {
    testBase.logon(AbstractTestBase.TEST_ADMIN_USER);
    owner = AbstractTestBase.initTestDB.addUser(prefix + "OwnerUser");
    fullUser1 = AbstractTestBase.initTestDB.addUser(prefix + "FullUser1");
    fullUser2 = AbstractTestBase.initTestDB.addUser(prefix + "FullUser2");
    fullUser3 = AbstractTestBase.initTestDB.addUser(prefix + "FullUser3");
    readonlyUser1 = AbstractTestBase.initTestDB.addUser(prefix + "ReadonlyUser1");
    readonlyUser2 = AbstractTestBase.initTestDB.addUser(prefix + "ReadonlyUser2");
    readonlyUser3 = AbstractTestBase.initTestDB.addUser(prefix + "ReadonlyUser3");
    trainingUser1 = AbstractTestBase.initTestDB.addUser(prefix + "TrainingUser1");
    trainingUser2 = AbstractTestBase.initTestDB.addUser(prefix + "TrainingUser2");
    trainingUser3 = AbstractTestBase.initTestDB.addUser(prefix + "TrainingUser3");
    noAccessUser = AbstractTestBase.initTestDB.addUser(prefix + "NoAccessUser");

    fullGroup1 = AbstractTestBase.initTestDB.addGroup(prefix + "FullGroup1", owner.getUsername(), fullUser1.getUsername());
    readonlyGroup1 = AbstractTestBase.initTestDB.addGroup(prefix + "ReadonlyGroup1", owner.getUsername(), readonlyUser1.getUsername());
    trainingGroup1 = AbstractTestBase.initTestDB.addGroup(prefix + "TrainingGroup1", owner.getUsername(), trainingUser1.getUsername());

    fullGroup2 = AbstractTestBase.initTestDB.addGroup(prefix + "FullGroup2", fullUser2.getUsername());
    readonlyGroup2 = AbstractTestBase.initTestDB.addGroup(prefix + "ReadonlyGroup2", readonlyUser2.getUsername());
    trainingGroup2 = AbstractTestBase.initTestDB.addGroup(prefix + "TrainingGroup2", trainingUser2.getUsername());

    testBase.logon(owner);
    SkillDO root = new SkillDO();

    root.setFullAccessGroupIds("" + fullGroup1.getId());
    root.setReadOnlyAccessGroupIds("" + readonlyGroup1.getId());
    root.setTrainingAccessGroupIds("" + trainingGroup1.getId());
    root.setTitle(prefix + ".title");
    root.setParent(null);
    final Integer rootId = (Integer) skillDao.save(root);

    root = skillDao.getById(rootId);

    final SkillDO skill = new SkillDO();

    skill.setFullAccessGroupIds("" + fullGroup1.getId());
    skill.setReadOnlyAccessGroupIds("" + readonlyGroup1.getId());
    skill.setTrainingAccessGroupIds("" + trainingGroup1.getId());
    skill.setTitle(prefix + ".title");
    skill.setParent(root);

    final Integer skillId = (Integer) skillDao.save(skill);

    return skillDao.getById(skillId);
  }

  public PFUserDO getFullUser1()
  {
    return fullUser1;
  }

  public PFUserDO getFullUser2()
  {
    return fullUser2;
  }

  public PFUserDO getFullUser3()
  {
    return fullUser3;
  }

  public PFUserDO getReadonlyUser1()
  {
    return readonlyUser1;
  }

  public PFUserDO getReadonlyUser2()
  {
    return readonlyUser2;
  }

  public PFUserDO getReadonlyUser3()
  {
    return readonlyUser3;
  }

  public PFUserDO getTrainingUser1()
  {
    return trainingUser1;
  }

  public PFUserDO getTrainingUser2()
  {
    return trainingUser2;
  }

  public PFUserDO getTrainingUser3()
  {
    return trainingUser3;
  }

  public PFUserDO getNoAccessUser()
  {
    return noAccessUser;
  }

  public GroupDO getFullGroup1()
  {
    return fullGroup1;
  }

  public GroupDO getReadonlyGroup1()
  {
    return readonlyGroup1;
  }

  public GroupDO getTrainingGroup1()
  {
    return trainingGroup1;
  }

  public GroupDO getFullGroup2()
  {
    return fullGroup2;
  }

  public GroupDO getReadonlyGroup2()
  {
    return readonlyGroup2;
  }

  public GroupDO getTrainingGroup2()
  {
    return trainingGroup2;
  }

  public PFUserDO getOwner()
  {
    return owner;
  }

}
