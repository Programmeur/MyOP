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

package org.projectforge.plugins.teamcal;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.projectforge.common.StringHelper;
import org.projectforge.continuousdb.UpdateEntry;
import org.projectforge.core.CronSetup;
import org.projectforge.database.xstream.XStreamSavingConverter;
import org.projectforge.plugins.core.AbstractPlugin;
import org.projectforge.plugins.teamcal.admin.TeamCalDO;
import org.projectforge.plugins.teamcal.admin.TeamCalDao;
import org.projectforge.plugins.teamcal.admin.TeamCalEditPage;
import org.projectforge.plugins.teamcal.admin.TeamCalListPage;
import org.projectforge.plugins.teamcal.admin.TeamCalRight;
import org.projectforge.plugins.teamcal.event.TeamEventAttendeeDO;
import org.projectforge.plugins.teamcal.event.TeamEventDO;
import org.projectforge.plugins.teamcal.event.TeamEventDao;
import org.projectforge.plugins.teamcal.event.TeamEventEditPage;
import org.projectforge.plugins.teamcal.event.TeamEventListPage;
import org.projectforge.plugins.teamcal.event.TeamEventRight;
import org.projectforge.plugins.teamcal.externalsubscription.TeamCalSubscriptionJob;
import org.projectforge.plugins.teamcal.externalsubscription.TeamEventExternalSubscriptionCache;
import org.projectforge.plugins.teamcal.integration.TeamCalCalendarFeedHook;
import org.projectforge.plugins.teamcal.integration.TeamCalCalendarFilter;
import org.projectforge.plugins.teamcal.integration.TeamCalCalendarPage;
import org.projectforge.plugins.teamcal.integration.TeamcalTimesheetPluginComponentHook;
import org.projectforge.plugins.teamcal.integration.TemplateCalendarProperties;
import org.projectforge.plugins.teamcal.integration.TemplateEntry;
import org.projectforge.plugins.teamcal.rest.TeamCalDaoRest;
import org.projectforge.plugins.teamcal.rest.TeamEventDaoRest;
import org.projectforge.registry.DaoRegistry;
import org.projectforge.registry.RegistryEntry;
import org.projectforge.user.GroupDO;
import org.projectforge.user.PFUserDO;
import org.projectforge.user.UserXmlPreferencesDO;
import org.projectforge.web.MenuItemDef;
import org.projectforge.web.MenuItemDefId;
import org.projectforge.web.MenuItemRegistry;
import org.projectforge.web.calendar.CalendarFeed;
import org.projectforge.web.rest.RestCallRegistry;
import org.projectforge.web.timesheet.TimesheetEditPage;
import org.projectforge.web.wicket.WicketApplication;

/**
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * @author M. Lauterbach (m.lauterbach@micromata.de)
 */
public class TeamCalPlugin extends AbstractPlugin
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TeamCalPlugin.class);

  public static final String ID = "teamCal";

  public static final String RESOURCE_BUNDLE_NAME = TeamCalPlugin.class.getPackage().getName() + ".TeamCalI18nResources";

  private static final Class< ? >[] PERSISTENT_ENTITIES = new Class< ? >[] { TeamCalDO.class, TeamEventDO.class, TeamEventAttendeeDO.class};

  /**
   * This dao should be defined in pluginContext.xml (as resources) for proper initialization.
   */
  private TeamCalDao teamCalDao;

  private TeamEventDao teamEventDao;

  @Override
  public Class< ? >[] getPersistentEntities()
  {
    return PERSISTENT_ENTITIES;
  }

  /**
   * @see org.projectforge.plugins.core.AbstractPlugin#initialize()
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void initialize()
  {
    // DatabaseUpdateDao is needed by the updater:
    TeamCalPluginUpdates.dao = getDatabaseUpdateDao();
    final RegistryEntry entry = new RegistryEntry(ID, TeamCalDao.class, teamCalDao, "plugins.teamcal");
    final RegistryEntry eventEntry = new RegistryEntry("teamEvent", TeamEventDao.class, teamEventDao, "plugins.teamcal.event");
    eventEntry.setNestedDOClasses(TeamEventAttendeeDO.class);

    // The CalendarDao is automatically available by the scripting engine!
    register(entry);
    register(eventEntry);


    // Register the web part:
    registerWeb(ID, TeamCalListPage.class, TeamCalEditPage.class);
    registerWeb(ID, TeamCalListPage.class, TeamCalEditPage.class, DaoRegistry.ADDRESS, false); // At second position (after Address entry)
    // for SearchPage.
    registerWeb("teamEvent", TeamEventListPage.class, TeamEventEditPage.class, ID, false); // At position after entry.

    addMountPage("teamCalendar", TeamCalCalendarPage.class);
    // Register the menu entry as sub menu entry of the misc menu:
    final MenuItemDef parentMenu = getMenuItemDef(MenuItemDefId.COMMON);
    // registerMenuItem(new MenuItemDef(parentMenu, ID, 7, "plugins.teamcal.menu", TeamCalCalendarPage.class));
    registerMenuItem(new MenuItemDef(parentMenu, ID + "List", 11, "plugins.teamcal.title.list", TeamCalListPage.class));
    final MenuItemDef menuItemDef = MenuItemRegistry.instance().get(MenuItemDefId.CALENDAR);
    menuItemDef.setPageClass(TeamCalCalendarPage.class);
    WicketApplication.setDefaultPage(TeamCalCalendarPage.class);
    // .setMobileMenu(ToDoMobileListPage.class, 10));

    // Define the access management:
    registerRight(new TeamCalRight());
    registerRight(new TeamEventRight());

    // All the i18n stuff:
    addResourceBundle(RESOURCE_BUNDLE_NAME);

    CalendarFeed.registerFeedHook(new TeamCalCalendarFeedHook());

    TimesheetEditPage.addPluginHook(new TeamcalTimesheetPluginComponentHook());

    RestCallRegistry.getInstance().register(TeamCalDaoRest.class).register(TeamEventDaoRest.class);

    TeamCalSubscriptionJob.setTeamCalDao(teamCalDao);
  }

  /**
   * @param teamCalDao the calendarDao to set
   * @return this for chaining.
   */
  public void setTeamCalDao(final TeamCalDao teamCalDao)
  {
    this.teamCalDao = teamCalDao;
  }

  public void setTeamEventDao(final TeamEventDao teamEventDao)
  {
    this.teamEventDao = teamEventDao;
  }

  /**
   * @see org.projectforge.plugins.core.AbstractPlugin#getInitializationUpdateEntry()
   */
  @Override
  public UpdateEntry getInitializationUpdateEntry()
  {
    return TeamCalPluginUpdates.getInitializationUpdateEntry();
  }

  /**
   * @see org.projectforge.plugins.core.AbstractPlugin#getUpdateEntries()
   */
  @Override
  public List<UpdateEntry> getUpdateEntries()
  {
    return TeamCalPluginUpdates.getUpdateEntries();
  }

  /**
   * Migrates the calendar ids of the filter templates and user/group id's of calendar access strings.
   * @see org.projectforge.plugins.core.AbstractPlugin#onBeforeRestore(org.projectforge.database.xstream.XStreamSavingConverter,
   *      java.lang.Object)
   */
  @Override
  public void onBeforeRestore(final XStreamSavingConverter xstreamSavingConverter, final Object obj)
  {
    if (obj instanceof UserXmlPreferencesDO) {
      final UserXmlPreferencesDO userPrefs = (UserXmlPreferencesDO) obj;
      if (TeamCalCalendarPage.USERPREF_KEY.equals(userPrefs.getKey()) == false) {
        return;
      }
      final Object userPrefsObj = userXmlPreferencesDao.deserialize(userPrefs, true);
      if (userPrefsObj == null || userPrefsObj instanceof TeamCalCalendarFilter == false) {
        return;
      }
      final TeamCalCalendarFilter filter = (TeamCalCalendarFilter) userPrefsObj;
      final List<TemplateEntry> templates = filter.getTemplateEntries();
      if (templates == null) {
        // Nothing to do.
        return;
      }
      for (final TemplateEntry template : templates) {
        final Set<TemplateCalendarProperties> calendarPropertiesSet = template.getCalendarProperties();
        if (calendarPropertiesSet != null && calendarPropertiesSet.size() > 0) {
          for (final TemplateCalendarProperties props : calendarPropertiesSet) {
            final Integer newCalendarId = xstreamSavingConverter.getNewIdAsInteger(TeamCalDO.class, props.getCalId());
            if (newCalendarId == null) {
              continue;
            }
            props.setCalId(newCalendarId);
          }
        }
        final Integer calendarId = template.getDefaultCalendarId();
        if (calendarId != null) {
          template.setDefaultCalendarId(xstreamSavingConverter.getNewIdAsInteger(TeamCalDO.class, calendarId));
        }
        final Integer timesheetUserId = template.getTimesheetUserId();
        if (timesheetUserId != null) {
          template.setTimesheetUserId(xstreamSavingConverter.getNewIdAsInteger(PFUserDO.class, timesheetUserId));
        }
      }
      userXmlPreferencesDao.serialize(userPrefs, filter);
      return;
    } else if (obj instanceof TeamCalDO) {
      log.info("Migrating " + obj);
      final TeamCalDO cal = (TeamCalDO) obj;
      cal.setFullAccessUserIds(updateIds(xstreamSavingConverter, PFUserDO.class, cal.getFullAccessUserIds()));
      cal.setReadonlyAccessUserIds(updateIds(xstreamSavingConverter, PFUserDO.class, cal.getReadonlyAccessUserIds()));
      cal.setMinimalAccessUserIds(updateIds(xstreamSavingConverter, PFUserDO.class, cal.getMinimalAccessUserIds()));
      cal.setFullAccessGroupIds(updateIds(xstreamSavingConverter, GroupDO.class, cal.getFullAccessGroupIds()));
      cal.setReadonlyAccessGroupIds(updateIds(xstreamSavingConverter, GroupDO.class, cal.getReadonlyAccessGroupIds()));
      cal.setMinimalAccessGroupIds(updateIds(xstreamSavingConverter, GroupDO.class, cal.getMinimalAccessGroupIds()));
    }
  }

  private String updateIds(final XStreamSavingConverter xstreamSavingConverter, final Class< ? > entityClass, final String oldIdsString)
  {
    if (StringUtils.isBlank(oldIdsString) == true) {
      return oldIdsString;
    }
    final int[] oldIds = StringHelper.splitToInts(oldIdsString, ",", false);
    if (oldIds == null || oldIds.length == 0) {
      return "";
    }
    final StringBuffer buf = new StringBuffer();
    String delimiter = "";
    for (final int oldId : oldIds) {
      final Integer newId = xstreamSavingConverter.getNewIdAsInteger(entityClass, oldId);
      if (newId == null) {
        // Can' be restored :-(
        continue;
      }
      buf.append(delimiter).append(newId);
      delimiter = ",";
    }
    return buf.toString();
  }

  @Override
  public void registerCronJob(final CronSetup cronSetup)
  {
    cronSetup.registerCronJob("teamCalAboJob", TeamCalSubscriptionJob.class, "0 */5 * * * ?");
    // do initial cache installation in a separated thread
    final Thread t = new Thread() {

      @Override
      public void run() {
        TeamEventExternalSubscriptionCache.instance().updateCache(teamCalDao);
      }
    };
    t.start();
  }
}
