$Version=2$
crmdark "widget/template"
    {
    label "widget/label" { fgcolor=white; }

    editbox "widget/editbox" { fgcolor=white; }

    textbutton "widget/textbutton"
	{
	background="/apps/kardia/images/bg/lsblue_btngradient.png";
	fgcolor1=white;
	fgcolor2=black;
	disable_color="#334466";
	}
    table "widget/table"
	{
	nodata_message_textcolor = "#808080";
	allow_selection = yes;
	show_selection = yes;
	initial_selection = no;
	demand_scrollbar = yes;
	overlap_scrollbar = yes;
	colsep = 0;
	titlebar = no;
	row_border_radius=6;
	inner_padding = 2;
	cellvspacing = 2;
	row1_bgcolor = "#181818";
	row2_bgcolor = "#202020";
	textcolor = white;
	rowhighlight_bgcolor = "#282828";
	rowhighlight_shadow_color = '#6080c0';
	rowhighlight_shadow_location = 'inside';
	rowhighlight_shadow_radius = 6;
	textcolorhighlight = white;
	}
    pane "widget/pane"
	{
	border_radius=8;
	border_color="#334466";
	style=bordered;
	}
    }
