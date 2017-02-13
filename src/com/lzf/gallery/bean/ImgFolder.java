package com.lzf.gallery.bean;

public class ImgFolder {

	public ImgFolder(String dir, String firstImagePath, String name, int count) {
		super();
		this.dir = dir;
		this.firstImagePath = firstImagePath;
		this.name = name;
		this.count = count;
	}

	public ImgFolder() {
		super();
	}

	private String dir; // 图片的文件夹路径

	private String firstImagePath; // 第一张图片的路径

	private String name; // 文件夹的名称

	private int count; // 图片的数量

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
		int lastIndexOf = this.dir.lastIndexOf("/");
		this.name = this.dir.substring(lastIndexOf + 1);
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
