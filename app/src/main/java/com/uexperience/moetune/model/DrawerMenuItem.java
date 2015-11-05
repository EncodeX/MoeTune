package com.uexperience.moetune.model;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 15/10/25
 * Project: MoeTune
 * Package: com.uexperience.moetune.model
 */
public class DrawerMenuItem {
	private int title;
	private int icon;
	private int iconSelected;

	public DrawerMenuItem(int icon, int iconSelected, int title) {
		this.icon = icon;
		this.iconSelected = iconSelected;
		this.title = title;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getIconSelected() {
		return iconSelected;
	}

	public void setIconSelected(int iconSelected) {
		this.iconSelected = iconSelected;
	}
}
