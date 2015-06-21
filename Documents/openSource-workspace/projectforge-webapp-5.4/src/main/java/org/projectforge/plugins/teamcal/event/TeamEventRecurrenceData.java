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

package org.projectforge.plugins.teamcal.event;

import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

import net.fortuna.ical4j.model.Recur;

import org.projectforge.calendar.CalendarUtils;
import org.projectforge.calendar.ICal4JUtils;
import org.projectforge.common.RecurrenceFrequency;

/**
 * @author Kai Reinhard (k.reinhard@micromata.de)
 */
public class TeamEventRecurrenceData implements Serializable
{
  private static final long serialVersionUID = -6258614682123676951L;

  private RecurrenceFrequency frequency = RecurrenceFrequency.NONE;

  private Date until;

  private int interval = 1;

  private boolean customized;

  private TimeZone timeZone;

  public TeamEventRecurrenceData(final TimeZone timeZone)
  {
    this.timeZone = timeZone;
  }

  public TeamEventRecurrenceData(final Recur recur, final TimeZone timeZone)
  {
    this.timeZone = timeZone;
    if (recur == null) {
      return;
    }
    this.interval = recur.getInterval();
    if (this.interval < 1) {
      this.interval = 1;
    }
    if (recur.getUntil() != null) {
      this.until = CalendarUtils.getEndOfDay(recur.getUntil(), timeZone);
    }
    this.frequency = ICal4JUtils.getFrequency(recur);
    if (this.interval > 1) {
      this.customized = true;
    }
  }

  /**
   * @return the frequency
   */
  public RecurrenceFrequency getFrequency()
  {
    return frequency;
  }

  /**
   * @param frequency the interval to set
   * @return this for chaining.
   */
  public TeamEventRecurrenceData setFrequency(final RecurrenceFrequency frequency)
  {
    this.frequency = frequency;
    return this;
  }

  /**
   * @return the until
   */
  public Date getUntil()
  {
    return until;
  }

  /**
   * @param until the until to set
   * @return this for chaining.
   */
  public TeamEventRecurrenceData setUntil(final Date until)
  {
    this.until = until;
    return this;
  }

  /**
   * @return the interval
   */
  public int getInterval()
  {
    return interval;
  }

  /**
   * If given interval is greater than 1 then the interval is set, otherwise the interval is set to -1 (default).
   * @param interval the interval to set
   * @return this for chaining.
   */
  public TeamEventRecurrenceData setInterval(final int interval)
  {
    if (interval > 1) {
      this.interval = interval;
    } else {
      this.interval = -1;
    }
    return this;
  }

  /**
   * @return the customized
   */
  public boolean isCustomized()
  {
    return customized;
  }

  /**
   * @param customized the customized to set
   * @return this for chaining.
   */
  public TeamEventRecurrenceData setCustomized(final boolean customized)
  {
    this.customized = customized;
    return this;
  }

  /**
   * @return the timeZone
   */
  public TimeZone getTimeZone()
  {
    return timeZone;
  }
}
