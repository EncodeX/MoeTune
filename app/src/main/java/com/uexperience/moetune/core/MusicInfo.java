package com.uexperience.moetune.core;

import org.json.JSONObject;

/**
 * Created with Android Studio.
 * Author: Enex Tapper
 * Date: 14-9-18
 * Project: MoeTune
 * Package: com.uexperience.moetune.core
 */
public class MusicInfo {
	private String upId;
	private String url;                     //曲目mp3音乐地址
	private int streamLength;               //曲目时长 单位秒
	private String streamTime;              //曲目时长 格式化
	private int fileSize;                   //mp3文件大小
	private String fileType;                //文件格式
	private int wikiId;                     //曲目所属专辑wiki_id
	private String wikiType;
	private MusicCoverUrl coverUrl;         //专辑封面
	private String title;                   //曲目标题 带曲号
	private String wikiTitle;               //专辑标题
	private String wikiUrl;                 //专辑页面
	private int subId;                      //曲目sub_id
	private String subType;
	private String subTitle;                //曲目标题 无曲号
	private String subUrl;                  //曲目网址
	private String artist;                  //曲目表演者
	private Boolean favWiki;                //是否收藏专辑
	private Boolean favSub;                 //是否收藏曲目

	public MusicInfo() {
	}

	public MusicInfo(String upId, String url, int streamLength, String streamTime,
	                 int fileSize, String fileType, int wikiId, String wikiType,
	                 MusicCoverUrl coverUrl, String title, String wikiTitle,
	                 String wikiUrl, int subId, String subType, String subTitle,
	                 String subUrl, String artist, Boolean favWiki, Boolean favSub) {
		this.upId = upId;
		this.url = url;
		this.streamLength = streamLength;
		this.streamTime = streamTime;
		this.fileSize = fileSize;
		this.fileType = fileType;
		this.wikiId = wikiId;
		this.wikiType = wikiType;
		this.coverUrl = coverUrl;
		this.title = title;
		this.wikiTitle = wikiTitle;
		this.wikiUrl = wikiUrl;
		this.subId = subId;
		this.subType = subType;
		this.subTitle = subTitle;
		this.subUrl = subUrl;
		this.artist = artist;
		this.favWiki = favWiki;
		this.favSub = favSub;
	}

	public MusicInfo(JSONObject object){
		this.upId = object.optString("up_id");
		this.url = urlReplace(object.optString("url"));
		this.streamLength = object.optInt("stream_length");
		this.streamTime = object.optString("stream_time");
		this.fileSize = object.optInt("file_size");
		this.fileType = object.optString("file_type");
		this.wikiId = object.optInt("wiki_id");
		this.wikiType = object.optString("wiki_type");
		this.title = symbolReplace(object.optString("title"));
		this.wikiTitle = symbolReplace(object.optString("wiki_title"));
		this.wikiUrl = urlReplace(object.optString("wiki_url"));
		this.subId = object.optInt("sub_id");
		this.subType = object.optString("sub_type");
		this.subTitle = symbolReplace(object.optString("sub_title"));
		this.subUrl = urlReplace(object.optString("sub_url"));
		this.artist = symbolReplace(object.optString("artist"));

		JSONObject jsonCoverUrl = object.optJSONObject("cover");
		this.coverUrl = new MusicCoverUrl(
				urlReplace(jsonCoverUrl.optString("small")),
				urlReplace(jsonCoverUrl.optString("medium")),
				urlReplace(jsonCoverUrl.optString("square")),
				urlReplace(jsonCoverUrl.optString("large"))
		);

		// Todo 收藏暂时不处理
//		this.favWiki = favWiki;
//		this.favSub = favSub;
	}

	public String getUpId() {
		return upId;
	}

	public void setUpId(String upId) {
		this.upId = upId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStreamLength() {
		return streamLength;
	}

	public void setStreamLength(int streamLength) {
		this.streamLength = streamLength;
	}

	public String getStreamTime() {
		return streamTime;
	}

	public void setStreamTime(String streamTime) {
		this.streamTime = streamTime;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getWikiId() {
		return wikiId;
	}

	public void setWikiId(int wikiId) {
		this.wikiId = wikiId;
	}

	public String getWikiType() {
		return wikiType;
	}

	public void setWikiType(String wikiType) {
		this.wikiType = wikiType;
	}

	public MusicCoverUrl getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(MusicCoverUrl coverUrl) {
		this.coverUrl = coverUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWikiTitle() {
		return wikiTitle;
	}

	public void setWikiTitle(String wikiTitle) {
		this.wikiTitle = wikiTitle;
	}

	public String getWikiUrl() {
		return wikiUrl;
	}

	public void setWikiUrl(String wikiUrl) {
		this.wikiUrl = wikiUrl;
	}

	public int getSubId() {
		return subId;
	}

	public void setSubId(int subId) {
		this.subId = subId;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getSubUrl() {
		return subUrl;
	}

	public void setSubUrl(String subUrl) {
		this.subUrl = subUrl;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public Boolean getFavWiki() {
		return favWiki;
	}

	public void setFavWiki(Boolean favWiki) {
		this.favWiki = favWiki;
	}

	public Boolean getFavSub() {
		return favSub;
	}

	public void setFavSub(Boolean favSub) {
		this.favSub = favSub;
	}

	private static String urlReplace(String url){
		return url.replaceAll("\\\\/","/");
	}

	private static String symbolReplace(String str){
		str = str.replaceAll("&quot;","\"");
		str = str.replaceAll("&amp;","&");
		str = str.replaceAll("&lt;","<");
		str = str.replaceAll("&gt;",">");
		str = str.replaceAll("&nbsp;"," ");
		str = str.replaceAll("&#039;","'");

		return str;
	}
}
