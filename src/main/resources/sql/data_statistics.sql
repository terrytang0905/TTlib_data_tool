/*==============================================================*/
/* Table: oms_device_statistics                                       */
/*==============================================================*/
drop table if exists oms_device_statistics;
create table oms_device_statistics
(
   ID                   int(20) auto_increment not null,
   DEVICEID             varchar(100),
   DEVICE_TYPE          varchar(50),
   CC                   varchar(20),
   SOURCE               varchar(20),
   CHANNEL              varchar(50),
   VERSION              varchar(20),
   NETWORK              varchar(20),
   IP           		varchar(50),
   OPERATE_TYPE         int(5),
   ISUSABLE             int(1),
   CREATE_TIME          datetime,
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_device_statistics comment 'device statistics data';

/*==============================================================*/
/* Table: oms_reguser_statistics                                       */
/*==============================================================*/
drop table if exists oms_reguser_statistics;
create table oms_reguser_statistics
(
   ID                    int(20) auto_increment not null,
   DEVICEID              varchar(100),
   SOURCE                varchar(20),
   VERSION               varchar(20),
   NETWORK               varchar(20),
   IP                    varchar(50),
   USERID                varchar(50) not null,
   REGISTER_TYPE         varchar(20),
   REGISTER_TIME         datetime,
   USER_NOTECOUNT        int(10),
   NEW_NOTECOUNT         int(10),
   USER_CATECOUNT        int(10),
   USER_TAGCOUNT         int(10),
   USER_RESOURCECOUNT    int(10),
   USER_LOGINNUM         int(10),
   USER_SYNCNUM          int(10),
   LAST_LOGIN_TIME       datetime,
   LAST_SYNC_TIME        datetime,  
   SYSTEM_TIME           datetime,
   primary key (ID)
);
alter table oms_reguser_statistics comment 'user register statistics';

/*==============================================================*/
/* Table: oms_user_daystatistics                                       */
/*==============================================================*/
drop table if exists oms_user_daystatistics;
create table oms_user_daystatistics
(
   ID                    int(20) auto_increment not null,
   DEVICEID              varchar(100),
   SOURCE                varchar(20),
   VERSION               varchar(20),
   NETWORK               varchar(20),
   IP                    varchar(50),
   USERID                varchar(50),
   NOTECOUNT			 int(10), 
   CATECOUNT             int(10), 
   TAGCOUNT				 int(10), 
   RESOURCECOUNT         int(10), 
   DAY_LOGINNUM          int(10), 
   DAY_SYNCNUM           int(10),
   CLIENT_DATATIME       datetime,  
   SYSTEM_TIME           datetime,
   primary key (ID)
);
alter table oms_user_daystatistics comment 'user day statistics data';

/*==============================================================*/
/* Table: oms_useractive_statistics                                       */
/*==============================================================*/
drop table if exists oms_useractive_statistics;
create table oms_useractive_statistics
(
   ID                   int(20) auto_increment not null,
   DEVICEID             varchar(100),
   SOURCE               varchar(20),
   VERSION              varchar(20),
   NETWORK              varchar(20),
   IP                   varchar(50),
   OPERATE_TYPE         int(5),
   USERID               varchar(50),
   LOGIN_TYPE           varchar(20),
   ACCESS_TIME          datetime,
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_useractive_statistics comment 'user active open/close operate data';

/*==============================================================*/
/* Table: oms_userlogin_statistic                                        */
/*==============================================================*/
drop table if exists oms_userlogin_statistics;
create table oms_userlogin_statistics
(
   ID                   int(20) auto_increment not null,
   DEVICEID             varchar(100),
   SOURCE               varchar(20),
   VERSION              varchar(20),
   NETWORK              varchar(20),
   IP                   varchar(50),
   OPERATE_TYPE         int(5),
   USERID               varchar(50) not null,
   LOGIN_TYPE           varchar(20),
   LOGIN_TIME           datetime,
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_userlogin_statistics comment 'user login and logout data';

/*==============================================================*/
/* Table: oms_usersync_statistics                                        */
/*==============================================================*/
drop table if exists oms_usersync_statistics;
create table oms_usersync_statistics
(
   ID                   int(20) auto_increment not null,
   DEVICEID             varchar(100),
   SOURCE               varchar(20),
   VERSION              varchar(20),
   NETWORK              varchar(20),
   IP                   varchar(50),
   OPERATE_TYPE         int(5),
   USERID               varchar(50) not null,
   LOGIN_TYPE           varchar(20),
   SYNC_TIME            datetime,
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_usersync_statistics comment 'user sync and commit data';

/*==============================================================*/
/* Table: oms_overall_statistics                               */
/*==============================================================*/
drop table if exists oms_overall_statistics;
create table oms_overall_statistics
(
   ID                   int(20) auto_increment not null,
   ACTIVE_USERNUM       int(20),
   REGISTER_USERNUM     int(20),
   OFFLINE_USERNUM      int(20),
   LOGIN_USERNUM        int(20),
   SYNC_USERNUM         int(100),
   ALL_NOTECOUNT        int(50),
   ALL_RESOURCECOUNT    int(50),
   TASKCOUNT			int(10), 
   STATISTICS_TIME		datetime,
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_overall_statistics comment 'overall data statistics';

/*==============================================================*/
/* Table: oms_message_version_statistics                                        */
/*==============================================================*/
drop table if exists oms_message_version_statistics;
create table oms_message_version_statistics
(
   ID                   int(20) auto_increment not null,
   MSGID             	varchar(20),
   VERSION              varchar(20),
   VALID_COUNT          int(20),
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_message_version_statistics comment 'message version statistics';

/*==============================================================*/
/* Table: oms_message_channel_statistics                                        */
/*==============================================================*/
drop table if exists oms_message_channel_statistics;
create table oms_message_channel_statistics
(
   ID                   int(20) auto_increment not null,
   MSGID             	varchar(20),
   CHANNEL              varchar(30),
   VALID_COUNT          int(20),
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_message_channel_statistics comment 'message channel statistics';

/*==============================================================*/
/* Table: oms_message_device_statistics                                        */
/*==============================================================*/
drop table if exists oms_message_device_statistics;
create table oms_message_device_statistics
(
   ID                   int(20) auto_increment not null,
   MSGID             	varchar(20),
   DEVICE_TYPE          varchar(20),
   VALID_COUNT          int(20),
   SYSTEM_TIME          datetime,
   primary key (ID)
);
alter table oms_message_device_statistics comment 'message device type statistics';