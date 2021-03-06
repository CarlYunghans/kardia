$Version=2$
plugin_rcpt_app_historicgiving "widget/page"
    {
    title = "Historic/Mail Giving Export";
    width=580;
    height=553;
    background="/apps/kardia/images/bg/light_bgnd.jpg";
    widget_template = "/apps/kardia/tpl/kardia-system.tpl", runserver("/apps/kardia/tpl/" + user_name() + ".tpl");
    func_name = "Historic Giving Export to SiteStacker";
    func_description = "Export historic and offline gift data to SiteStacker";
    func_enable = "has_endorsement('kardia:gift_amt', 'kardia:ledger:*') and (select count(1) from /apps/kardia/data/Kardia_DB/a_config/rows where :a_config_name = 'GiftImport_SS' and convert(integer, :a_config_value) > 0) > 0";
    require_one_endorsement="kardia:gift_manage","kardia:gift_amt";
    endorsement_context=runserver("kardia:ledger:" + :this:ledger + ":");

    ledger "widget/parameter" { type=string; default=null; allowchars="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"; deploy_to_client=yes; }
    period "widget/parameter" { type=string; default=null; deploy_to_client=yes; }
    costctr "widget/parameter" { type=string; default=null; deploy_to_client=yes; }

    rpt_form "widget/form"
	{
	allow_nodata = no;
	allow_new = no;
	auto_focus = no;

	vb2 "widget/vbox"
	    {
	    x=32;y=8;width=514;height=496;
	    spacing=4;
	    lbl_opt "widget/label" { height=30; font_size=16; text="Historic Giving Export Options:"; align=center; }

	    pn_sep1 "widget/pane" { height=2; style=lowered; }

	    f_ledger "widget/component" { width=350; height=24; path="/sys/cmp/smart_field.cmp"; field='ledger'; ctl_type=label; text='Ledger:'; value=runserver(:this:ledger); form=rpt_form; label_width=120; }

	    f_costctr "widget/component" { width=350; height=24; path="/apps/kardia/modules/base/editbox_tree.cmp"; field="costctr"; popup_source=runserver("/apps/kardia/modules/gl/costctrs.qyt/" + :this:ledger + "/"); popup_text="Choose Cost Center:"; text="Cost Center:"; attach_point=editbox; empty_desc = "optional"; label_width=120; }

	    f_period "widget/component" { width=350; height=24; path="/sys/cmp/smart_field.cmp"; field="period"; text="Beginning Period:"; empty_desc = "required"; ctl_type=dropdown; sql=runserver("select :a_period_desc + ' - ' + :a_period, :a_period from /apps/kardia/data/Kardia_DB/a_period/rows where :a_summary_only = 0 and :a_ledger_number = " + quote(:this:ledger) + " order by :a_start_date desc"); label_width=120; }
	    f_startday "widget/component" { width=350; height=24; path="/sys/cmp/smart_field.cmp"; field="startday"; text="Start Day:"; ctl_type=editbox; empty_desc = "optional: 1 - 31"; label_width=120; }
	    f_endperiod "widget/component" { width=350; height=24; path="/sys/cmp/smart_field.cmp"; field="endperiod"; text="Ending Period:"; empty_desc = "required"; ctl_type=dropdown; sql=runserver("select :a_period_desc + ' - ' + :a_period, :a_period from /apps/kardia/data/Kardia_DB/a_period/rows where :a_summary_only = 0 and :a_ledger_number = " + quote(:this:ledger) + " order by :a_start_date desc"); label_width=120; }
	    f_endday "widget/component" { width=350; height=24; path="/sys/cmp/smart_field.cmp"; field="endday"; text="End Day:"; ctl_type=editbox; empty_desc = "optional: 1 - 31"; label_width=120; }

	    sep1 "widget/autolayoutspacer" { height=4; }

	    f_docfmt "widget/component"
		{ 
		width=350; height=24; 
		path="/sys/cmp/smart_field.cmp"; 
		field='user_document_format'; 
		ctl_type=dropdown; 
		text='Format:'; 
		sql = runserver("select :t:type_description + ' (' + :t:type_name + ')', :t:type_name from /sys/cx.sysinfo/osml/types t, /sys/cx.sysinfo/prtmgmt/output_types ot where :t:type_name = :ot:type order by :t:type_description");
		form=rpt_form;
		label_width=120;
		}
	    }

	vb3 "widget/vbox"
	    {
	    x=32;y=512;width=514;height=40;
	    align=bottom;

	    spacing=4;

	    pn_sep2 "widget/pane" { height=2; style=lowered; }

	    ctls_hbox "widget/hbox"
		{
		height=32;
		spacing=4;
		rpt_print "widget/textbutton"
		    {
		    width=90;
		    text="Print";
		    //enabled = runclient(char_length(:f_year:content) > 0);
		    rpt_print_cn "widget/connector"
			{
			event="Click";
			target="rpt_form";
			action="Submit";
			Target=runclient("plugin_rcpt_app_historicgiving");
			NewPage=1;
			Source=runclient("/apps/kardia/modules/eg_wmtek/historic_giving.rpt");
			Width=800;
			Height=600;
			document_format=runclient(isnull(:f_docfmt:value, 'text/csv'));
			document_format2=runclient(isnull(:f_docfmt:value, 'text/csv'));
			}
		    }
		rpt_cancel "widget/textbutton"
		    {
		    width=90;
		    text="Cancel";
		    rpt_cancel_cn "widget/connector" { event="Click"; target="plugin_rcpt_app_historicgiving"; action="Close"; }
		    }
		}
	    }
	}
    }
