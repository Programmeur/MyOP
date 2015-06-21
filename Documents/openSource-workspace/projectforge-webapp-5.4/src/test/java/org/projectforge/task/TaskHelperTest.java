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

package org.projectforge.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.projectforge.fibu.ProjektDO;
import org.projectforge.fibu.ProjektDao;
import org.projectforge.fibu.kost.Kost2ArtDO;
import org.projectforge.fibu.kost.Kost2DO;
import org.projectforge.test.TestBase;


public class TaskHelperTest extends TestBase
{
  private ProjektDao projektDao;
  
  private TaskTree taskTree;

  public void setProjektDao(ProjektDao projektDao)
  {
    this.projektDao = projektDao;
  }
  
  public void setTaskTree(TaskTree taskTree)
  {
    this.taskTree = taskTree;
  }

  @Test
  public void normalizeKost2BlackWhiteList()
  {
    final TaskDO task = new TaskDO().setKost2BlackWhiteList(null);
    assertNull(TaskHelper.normalizeKost2BlackWhiteList(task));
    assertEquals("", TaskHelper.normalizeKost2BlackWhiteList(task.setKost2BlackWhiteList("")));
    assertEquals("1", TaskHelper.normalizeKost2BlackWhiteList(task.setKost2BlackWhiteList("1")));
    assertEquals(".89,45,5.212.01.12", TaskHelper.normalizeKost2BlackWhiteList(task.setKost2BlackWhiteList("5.212.01.12, 45;  .89")));
    assertEquals(".89,45,5.212.01.12", TaskHelper.normalizeKost2BlackWhiteList(task.setKost2BlackWhiteList("5.212.01.12,, 45;  .89")));
    assertEquals(".89,45,5.212.01.12", TaskHelper.normalizeKost2BlackWhiteList(task.setKost2BlackWhiteList("5.212.01.12, , 45;  .89,45")));
  }

  @Test
  public void addKost2()
  {
    logon(TEST_FINANCE_USER);
    final TaskDO task1 = initTestDB.addTask("addKost2", "root");
    final ProjektDO projekt = new ProjektDO().setName("addKost2").setInternKost2_4(128).setNummer(5).setTask(task1);
    projektDao.save(projekt);
    final Kost2ArtDO kost2Art = new Kost2ArtDO().withId(42);
    final Kost2DO kost = new Kost2DO().setNummernkreis(4).setBereich(128).setTeilbereich(5).setKost2Art(kost2Art);
    assertEquals("42", TaskHelper.addKost2(taskTree, task1, kost));
    assertEquals("12,42,6.001.02.89", TaskHelper.addKost2(taskTree, task1.setKost2BlackWhiteList("12,6.001.02.89,12"), kost));
    final TaskDO task2 = new TaskDO();
    assertEquals("4.128.05.42", TaskHelper.addKost2(taskTree, task2, kost));
    task2.setKost2BlackWhiteList("12,6.001.02.89");
    assertEquals("12,4.128.05.42,6.001.02.89", TaskHelper.addKost2(taskTree, task2, kost));
  }
}
