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

package org.projectforge.web.calendar;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.projectforge.web.dialog.ModalDialog;
import org.projectforge.web.wicket.WicketUtils;
import org.projectforge.web.wicket.components.SingleButtonPanel;
import org.projectforge.web.wicket.flowlayout.FieldsetPanel;

import de.micromata.wicket.ajax.AjaxCallback;

/**
 * @author M. Lauterbach (m.lauterbach@micromata.de)
 * @author Kai Reinhard (k.reinhard@micromata.de)
 * 
 */
public abstract class AbstractICSExportDialog extends ModalDialog
{
  private static final long serialVersionUID = 1579507911025462251L;

  protected TextArea<String> urlTextArea;

  /**
   * @param id
   * @param titleModel
   */
  public AbstractICSExportDialog(final String id, final IModel<String> titleModel)
  {
    super(id);
    if (titleModel != null) {
      setTitle(titleModel);
    }
    setBigWindow();
  }

  @SuppressWarnings("serial")
  public void redraw()
  {
    clearContent();
    {
      gridBuilder.newSecurityAdviceBox(Model.of(getString("calendar.icsExport.securityAdvice")));
      addFormFields();
      final FieldsetPanel fs = gridBuilder.newFieldset(getString("calendar.abonnement.url")).setLabelSide(false);
      urlTextArea = new TextArea<String>(fs.getTextAreaId(), new Model<String>() {
        @Override
        public String getObject()
        {
          return WicketUtils.getAbsoluteContextPath() + getUrl();
        };
      });
      urlTextArea.setOutputMarkupId(true);
      fs.add(urlTextArea);
      urlTextArea.add(AttributeModifier.replace("onClick", "$(this).select();"));
    }
  }

  protected abstract String getUrl();

  /**
   * Does nothing at default. Override this method for inserting form fields before url text-area.
   */
  protected void addFormFields()
  {
  }

  @SuppressWarnings("serial")
  @Override
  public void init()
  {
    appendNewAjaxActionButton(new AjaxCallback() {

      @Override
      public void callback(final AjaxRequestTarget target)
      {
        target.appendJavaScript("setTimeout(\"window.location.href='" + WicketUtils.getAbsoluteContextPath() + getUrl() + "'\", 100);");
        close(target);
      }
    }, getString("download"), SingleButtonPanel.NORMAL);
    init(new Form<String>(getFormId()));
  }
}
