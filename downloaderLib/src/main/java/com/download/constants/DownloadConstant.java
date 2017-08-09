
package com.download.constants;

public class DownloadConstant {

    /**
     * wait download status
     */
    public static final int STATUS_READY = 1;
    /**
     * start download status
     */
    public static final int STATUS_START = 2;
    /**
     * downloading status
     */
    public static final int STATUS_DOWNLOADING = 3;
    /**
     * pause download status
     */
    public static final int STATUS_PAUSED = 4;
    /**
     * donwload complete status
     */
    public static final int STATUS_COMPLETE = 5;
    /**
     * download canceled status
     */
    public static final int STATUS_CANCELLED = 6;
    /**
     * download error status
     */
    public static final int STATUS_ERROR = 7;

    /**
     * error code init fail
     * 初始化失败，比如数据库中字段缺失
     */
    public static final int ERROR_CODE_INIT_FAIL = 0;
    /**
     * error code url invalid
     * url不正确
     */
    public static final int ERROR_CODE_URL = 1;
    /**
     * error code create dir fail
     * 创建文件夹失败
     */
    public static final int ERROR_CODE_CREATE_DIR = 2;
    /**
     * error code remote file is empty
     */
    public static final int ERROR_CODE_EMPTY_TARGET = 3;
    /**
     * error code network anvalid
     * 网络异常
     */
    public static final int ERROR_CODE_NETWORK = 4;
    /**
     * error code socket time out
     * 链接超时
     */
    public static final int ERROR_CODE_SOCKET_TIME_OUT = 5;
    /**
     * error code open connection fail
     * 链接失败
     */
    public static final int ERROR_CODE_OPEN_CONNECTION = 6;
    /**
     * error code connect
     * 链接失败
     */
    public static final int ERROR_CODE_CONNECT = 7;
    /**
     * error code response error
     * 响应失败
     */
    public static final int ERROR_CODE_GET_RESPONSE_CODE = 8;
    /**
     * error code file not found
     * 文件不存在
     */
    public static final int ERROR_CODE_FILE_NOT_FOUND = 9;
    /**
     * error code seek file fail
     * 文件跳转失败
     */
    public static final int ERROR_CODE_FILE_SEEK = 10;
    /**
     * error code input stream
     * 从远程读取文件失败
     */
    public static final int ERROR_CODE_GET_INPUT_STREAM = 11;
    /**
     * error code write file
     * 本地读写文件失败
     */
    public static final int ERROR_CODE_FILE_WRITE = 12;
    /**
     * error code close file
     * 文件流失败
     */
    public static final int ERROR_CODE_FILE_CLOSE = 13;
    /**
     * error code io
     */
    public static final int ERROR_CODE_IO = 14;
    /**
     * error code no sdcard
     */
    public static final int ERROR_CODE_NO_SDCARD = 15;
    public static final int ERROR_CODE_INVALID_MIMETYPE =16;
    /**
     * error code unkown
     * 不知道的异常
     */
    public static final int ERROR_CODE_UNKOWN = 17;

    public static final String ERROR_MSG_INIT_FAIL = "init fail ";
    public static final String ERROR_MSG_URL = "invalid download url ";
    public static final String ERROR_MSG_CREATE_DIR = "create directory error ";
    public static final String ERROR_MSG_EMPTY_TARGET = "empty app target on server ";
    public static final String ERROR_MSG_NETWORK = "network error ";
    public static final String ERROR_MSG_SOCKET_TIME_OUT = "socket time out ";
    public static final String ERROR_MSG_OPEN_CONNECTION = "open connection fail ";
    public static final String ERROR_MSG_CONNECT = "connect fail ";
    public static final String ERROR_MSG_GET_RESPONSE_CODE = "get response code fail ";
    public static final String ERROR_MSG_FILE_NOT_FOUND = "file not found ";
    public static final String ERROR_MSG_FILE_SEEK = "file seek fail ";
    public static final String ERROR_MSG_GET_INPUT_STREAM = "get input stream fail ";
    public static final String ERROR_MSG_FILE_WRITE = "file write fail ";
    public static final String ERROR_MSG_FILE_CLOSE = "file close fail ";
    public static final String ERROR_MSG_IO = "IOException ";
    public static final String ERROR_MSG_UNKOWN = "unknown error ";
}
