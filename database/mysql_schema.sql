/*==============================================================*/
/* Table: jspbb_access                                          */
/*==============================================================*/
create table jspbb_access
(
    id_                  bigint not null comment '访问ID',
    user_id_             bigint comment '用户ID',
    date_                datetime not null comment '访问日期',
    date_ymd_            char(6) not null comment '访问日期(字符串格式yyMMdd)',
    date_ymdh_           char(8) not null comment '访问日期(字符串格式yyMMddHH)',
    date_ymdhi_          char(10) not null comment '访问日期(字符串格式yyMMddHHmm)',
    cookie_              bigint not null default 0 comment 'COOKIE值',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    browser_             varchar(100) comment '浏览器',
    os_                  varchar(100) comment '操作系统',
    device_              char(1) comment '设备(0:未知;1:COMPUTER,2:MOBILE,3:TABLET)',
    country_             varchar(100) comment '国家',
    province_            varchar(100) comment '省份',
    city_                varchar(100) comment '城市',
    provider_            varchar(100) comment '接入提供商',
    source_              varchar(100) not null comment '来源域名',
    primary key (id_)
);

alter table jspbb_access comment '访问表';

/*==============================================================*/
/* Table: jspbb_access_ext                                      */
/*==============================================================*/
create table jspbb_access_ext
(
    id_                  bigint not null comment '访问ID',
    url_                 varchar(255) not null comment '访问URL',
    referrer_            varchar(255) comment '来源URL',
    user_agent_          varchar(450) comment '用户代理',
    primary key (id_)
);

alter table jspbb_access_ext comment '访问扩展表';

/*==============================================================*/
/* Table: jspbb_action                                          */
/*==============================================================*/
create table jspbb_action
(
    id_                  bigint not null comment '动作ID',
    user_id_             bigint not null comment '用户ID',
    ref_type_            varchar(20) not null comment '动作对象类型(question_up,answer_down,等)',
    ref_id_              bigint not null comment '动作对象ID',
    created_             datetime not null comment '创建日期',
    primary key (id_)
);

alter table jspbb_action comment '动作表';

/*==============================================================*/
/* Table: jspbb_answer                                          */
/*==============================================================*/
create table jspbb_answer
(
    id_                  bigint not null comment '回答ID',
    question_id_         bigint not null comment '问题ID',
    user_id_             bigint not null comment '创建用户ID',
    edit_user_id_        bigint comment '修改用户ID',
    created_             datetime not null comment '创建日期',
    edit_date_           datetime comment '修改日期',
    status_              int not null default 0 comment '状态(0:正常,1:待审,2:屏蔽,3:删除)',
    ups_                 int not null default 0 comment '顶',
    downs_               int not null default 0 comment '踩',
    accept_date_         datetime comment '采纳日期',
    accepted_            char(1) not null default '0' comment '是否采纳(0:否,1:是)',
    comment_count_       int not null default 0 comment '评论次数',
    favorite_count_      int not null default 0 comment '收藏次数',
    primary key (id_)
);

alter table jspbb_answer comment '答案表';

/*==============================================================*/
/* Table: jspbb_answer_ext                                      */
/*==============================================================*/
create table jspbb_answer_ext
(
    id_                  bigint not null comment '回答ID',
    markdown_            mediumtext not null comment '正文markdown',
    text_                mediumtext not null comment '正文html',
    edit_count_          int not null default 0 comment '修改次数',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_answer_ext comment '回答扩展表';

/*==============================================================*/
/* Table: jspbb_answer_history                                  */
/*==============================================================*/
create table jspbb_answer_history
(
    id_                  bigint not null comment '答案历史记录ID',
    answer_id_           bigint not null comment '回答ID',
    user_id_             bigint not null comment '用户ID',
    markdown_            mediumtext not null comment '正文markdown',
    text_                mediumtext not null comment '正文html',
    date_                datetime not null comment '日期',
    type_                int not null default 0 comment '类型(0:历史备份;1:修改申请)',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_answer_history comment '答案历史记录表';

/*==============================================================*/
/* Table: jspbb_attach                                          */
/*==============================================================*/
create table jspbb_attach
(
    id_                  bigint not null comment '附件ID',
    user_id_             bigint not null comment '用户ID',
    name_                varchar(100) not null comment '文件名',
    url_                 varchar(255) not null comment '文件URL',
    orig_name_           varchar(255) comment '原始文件名',
    length_              bigint not null default 0 comment '文件长度',
    date_                datetime not null comment '长传日期',
    used_                char(1) not null default '0' comment '是否使用(0:否,1:是)',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_attach comment '附件表';

/*==============================================================*/
/* Table: jspbb_attach_ref                                      */
/*==============================================================*/
create table jspbb_attach_ref
(
    attach_id_           bigint not null comment '附件ID',
    ref_type_            varchar(50) not null comment '引用对象类型',
    ref_id_              bigint not null comment '引用对象ID',
    primary key (attach_id_, ref_type_, ref_id_)
);

alter table jspbb_attach_ref comment '附件引用表';

/*==============================================================*/
/* Table: jspbb_comment                                         */
/*==============================================================*/
create table jspbb_comment
(
    id_                  bigint not null comment '评论ID',
    parent_id_           bigint comment '上级评论ID',
    user_id_             bigint not null comment '用户ID',
    edit_user_id_        bigint comment '修改用户ID',
    ref_type_            varchar(20) not null comment '评论对象类型',
    ref_id_              bigint not null comment '评论对象ID',
    created_             datetime not null comment '创建日期',
    edit_date_           datetime comment '修改日期',
    markdown_            mediumtext not null comment '正文markdown',
    text_                mediumtext not null comment '正文html',
    status_              int not null default 0 comment '状态(0:正常,1:待审,2:屏蔽,3:删除)',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_comment comment '评论表';

/*==============================================================*/
/* Table: jspbb_config                                          */
/*==============================================================*/
create table jspbb_config
(
    name_                varchar(50) not null comment '名称',
    value_               mediumtext comment '值',
    primary key (name_)
);

alter table jspbb_config comment '配置表';

/*==============================================================*/
/* Table: jspbb_group                                           */
/*==============================================================*/
create table jspbb_group
(
    id_                  bigint not null comment '用户组ID',
    name_                varchar(100) not null comment '名称',
    description_         varchar(2000) comment '描述',
    type_                int not null default 2 comment '类型(0:预设,1:特殊,2:自动)',
    reputation           int not null default 0 comment '所需声望',
    question_max_        int not null default 2 comment '提问最大条数',
    question_within_     int not null default 240 comment '提问限制间隔(分钟)',
    answer_max_          int not null default 3 comment '回答最大条数',
    answer_within_       int not null default 240 comment '回答限制间隔(分钟)',
    comment_max_         int not null default 8 comment '评论最大条数',
    comment_within_      int not null default 240 comment '评论限制间隔(分钟)',
    message_max_         int not null default 20 comment '消息最大条数',
    message_within_      int not null default 240 comment '消息限制间隔(分钟)',
    upload_max_          int not null default 10 comment '上传最大长度(MB)',
    upload_within_       int not null default 24 comment '上传限制间隔(小时)',
    order_               int not null default 999999 comment '排序值',
    perms_               mediumtext comment '权限',
    is_trusted_          char(1) not null default '1' comment '是否可信用户(投票可加声誉)',
    primary key (id_)
);

alter table jspbb_group comment '用户组表';

/*==============================================================*/
/* Table: jspbb_ip_restrict                                     */
/*==============================================================*/
create table jspbb_ip_restrict
(
    ip_                  varchar(39) not null comment 'IP',
    name_                varchar(50) not null comment '名称',
    start_               datetime not null comment '开始时间',
    last_                datetime not null comment '最近一次时间',
    count_               bigint not null default 0 comment '数量',
    primary key (ip_, name_)
);

alter table jspbb_ip_restrict comment 'IP限制表';

/*==============================================================*/
/* Table: jspbb_message                                         */
/*==============================================================*/
create table jspbb_message
(
    id_                  bigint not null comment '消息ID',
    messagedetail_id_    bigint not null comment '消息内容ID',
    user_id_             bigint not null comment '用户ID',
    contact_user_id_     bigint not null comment '联系人用户ID',
    from_user_id_        bigint not null comment '发件用户ID',
    to_user_id_          bigint not null comment '收件用户ID',
    date_                datetime not null,
    sys_                 char(1) not null default '0' comment '是否系统消息(0:否,1:是)',
    type_                int not null default 1 comment '类型(1:收件,2:发件)',
    unread_              int not null default 1 comment '是否未读(0:未读,1:已读)',
    send_total_          int not null default 1 comment '发送总数',
    read_count_          int not null default 0 comment '已读数量',
    primary key (id_)
);

alter table jspbb_message comment '消息表';

/*==============================================================*/
/* Table: jspbb_message_detail                                  */
/*==============================================================*/
create table jspbb_message_detail
(
    id_                  bigint not null comment '消息内容ID',
    title_               varchar(255) comment '标题',
    markdown_            mediumtext not null comment '正文markdown',
    referred_count_      int not null default 2 comment '被引用数量',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_message_detail comment '消息内容表';

/*==============================================================*/
/* Table: jspbb_notification                                    */
/*==============================================================*/
create table jspbb_notification
(
    id_                  bigint not null comment '通知ID',
    user_id_             bigint not null comment '用户ID',
    type_                varchar(50) not null comment '类型',
    data_                varchar(50) not null default '0' comment '数据',
    created_             datetime not null comment '创建日期',
    body_                varchar(450) not null comment '内容',
    url_                 varchar(255) comment 'URL',
    primary key (id_)
);

alter table jspbb_notification comment '通知表';

/*==============================================================*/
/* Table: jspbb_question                                        */
/*==============================================================*/
create table jspbb_question
(
    id_                  bigint not null comment '问题ID',
    user_id_             bigint not null comment '创建用户ID',
    edit_user_id_        bigint comment '修改用户ID',
    active_user_id_      bigint not null comment '活跃用户ID',
    created_             datetime not null comment '创建日期',
    edit_date_           datetime comment '修改日期',
    active_date_         datetime not null comment '活跃日期',
    status_              int not null default 0 comment '状态(0:正常,1:待审,2:屏蔽,3:删除)',
    ups_                 int not null default 0 comment '顶',
    downs_               int not null default 0 comment '踩',
    answer_count_        int not null default 0 comment '回答次数',
    favorite_count_      int not null default 0 comment '收藏次数',
    comment_count_       int not null default 0 comment '评论次数',
    views_               bigint not null default 0 comment '浏览次数',
    primary key (id_)
);

alter table jspbb_question comment '问题表';

/*==============================================================*/
/* Table: jspbb_question_ext                                    */
/*==============================================================*/
create table jspbb_question_ext
(
    id_                  bigint not null comment '问题ID',
    title_               varchar(255) not null comment '标题',
    markdown_            mediumtext comment '正文markdown',
    text_                mediumtext comment '正文html',
    active_type_         varchar(50) not null default 'asked' comment '活跃类型(asked,answered,edited,commented)',
    edit_count_          int not null default 0 comment '修改次数',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_question_ext comment '问题扩展表';

/*==============================================================*/
/* Table: jspbb_question_history                                */
/*==============================================================*/
create table jspbb_question_history
(
    id_                  bigint not null comment '问题历史记录ID',
    question_id_         bigint not null comment '问题ID',
    user_id_             bigint not null comment '用户ID',
    title_               varchar(255) not null comment '标题',
    markdown_            mediumtext comment '正文markdown',
    text_                mediumtext comment '正文html',
    date_                datetime not null comment '日期',
    type_                int not null default 0 comment '类型(0:历史备份,1:修改申请)',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_question_history comment '问题历史记录表';

/*==============================================================*/
/* Table: jspbb_question_tag                                    */
/*==============================================================*/
create table jspbb_question_tag
(
    question_id_         bigint not null comment '问题ID',
    tag_id_              bigint not null comment '标签ID',
    primary key (question_id_, tag_id_)
);

alter table jspbb_question_tag comment '问题与标签关联表';

/*==============================================================*/
/* Table: jspbb_role                                            */
/*==============================================================*/
create table jspbb_role
(
    id_                  bigint not null comment '角色表',
    name_                varchar(100) not null comment '名称',
    description_         varchar(2000) comment '描述',
    perms_               mediumtext comment '权限值(使用;分隔，*代表拥有所有权限)',
    order_               int not null default 0 comment '排序值',
    primary key (id_)
);

alter table jspbb_role comment '角色表';

/*==============================================================*/
/* Table: jspbb_seq                                             */
/*==============================================================*/
create table jspbb_seq
(
    name_                varchar(50) not null comment '序列名称(通常为表名)',
    next_val_            bigint not null default 1 comment '下一个值',
    cache_size_          int not null default 0 comment '缓存数量(大于0时有效，等于0则由程序确定大小)',
    primary key (name_)
);

alter table jspbb_seq comment '主键序列表';

/*==============================================================*/
/* Table: jspbb_sms                                             */
/*==============================================================*/
create table jspbb_sms
(
    id_                  bigint not null comment '短信ID',
    type_                varchar(50) not null comment '类型(mobile,email)',
    usage_               varchar(50) not null comment '用途(signUp,modify,passwordReset)',
    receiver_            varchar(50) not null comment '名称(手机号码或邮箱地址)',
    code_                varchar(50) not null comment '验证码',
    send_date_           datetime not null comment '发送时间',
    try_count_           int not null default 0 comment '尝试次数(-1代表已使用)',
    status_              int not null default 0 comment '状态(0:未使用,1:验证正确,2:验证错误,3:尝试次数过多,4:过期)',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    primary key (id_)
);

alter table jspbb_sms comment '短信表';

/*==============================================================*/
/* Table: jspbb_tag                                             */
/*==============================================================*/
create table jspbb_tag
(
    id_                  bigint not null comment '标签ID',
    name_                varchar(50) not null comment '名称',
    description_         varchar(2000) comment '描述',
    created_             datetime not null comment '创建日期',
    usages_              int not null default 0 comment '使用次数',
    recommended_         char(1) not null default '0' comment '是否推荐(0:否,1:是)',
    primary key (id_)
);

alter table jspbb_tag comment '标签表';

/*==============================================================*/
/* Table: jspbb_user                                            */
/*==============================================================*/
create table jspbb_user
(
    id_                  bigint not null comment '用户ID',
    group_id_            bigint not null comment '用户组ID',
    username_            varchar(50) not null comment '用户名',
    mobile_              varchar(50) comment '手机号码',
    email_               varchar(50) comment '电子邮箱',
    created_             datetime not null comment '创建日期',
    status_              int not null default 0 comment '状态(0:正常,1:锁定,2:见习)',
    home_                varchar(50) comment '主页名',
    primary key (id_),
    unique key ak_username (username_),
    unique key ak_emial (email_),
    unique key ak_mobile (mobile_),
    unique key ak_home (home_)
);

alter table jspbb_user comment '用户表';

/*==============================================================*/
/* Table: jspbb_user_ext                                        */
/*==============================================================*/
create table jspbb_user_ext
(
    id_                  bigint not null comment '用户ID',
    password_            varchar(128) not null default '0' comment '密码',
    salt_                varchar(32) not null default '0' comment '密码混淆码',
    uploaded_length_     bigint not null default 0 comment '上传文件总长度',
    gender_              char(1) not null default 'n' comment '性别(m:男;f:女;n:保密)',
    picture_version_     int not null default 0 comment '头像版本号。0代表未上传头像',
    picture_url_         varchar(255) comment '头像url',
    title_               varchar(150) comment '个性签名',
    birthday_            datetime comment '出生日期',
    location_            varchar(255) comment '居住地',
    ip_                  varchar(39) not null default 'none' comment 'IP',
    ip_country_          varchar(100) comment 'IP国家',
    ip_province_         varchar(100) comment 'IP省份',
    ip_city_             varchar(100) comment 'IP城市',
    login_date_          datetime not null comment '登录日期',
    login_ip_            varchar(39) not null default 'none' comment '登录IP',
    login_ip_country_    varchar(100) comment '登录国家',
    login_ip_province_   varchar(100) comment '登录省份',
    login_ip_city_       varchar(100) comment '登录城市',
    login_count_         int not null default 0 comment '登录次数',
    primary key (id_)
);

alter table jspbb_user_ext comment '用户扩展表';

/*==============================================================*/
/* Table: jspbb_user_openid                                     */
/*==============================================================*/
create table jspbb_user_openid
(
    user_id_             bigint not null comment '用户ID',
    provider_            varchar(50) not null comment '提供商',
    openid_              varchar(100) not null comment 'OPEN ID',
    unionid_             varchar(100) comment '微信统一ID',
    display_name_        varchar(100) comment '显示名',
    picture_url_         varchar(255) comment '头像URL',
    gender_              char(1) not null default 'm' comment '性别(m:男,f:女,n:保密)',
    large_picture_url_   varchar(255) comment '大头像URL',
    primary key (user_id_, provider_)
);

alter table jspbb_user_openid comment '用户OpenID表';

/*==============================================================*/
/* Table: jspbb_user_restrict                                   */
/*==============================================================*/
create table jspbb_user_restrict
(
    user_id_             bigint not null comment '用户ID',
    name_                varchar(50) not null comment '名称',
    start_               datetime not null comment '开始时间',
    last_                datetime not null comment '最近一次时间',
    count_               bigint not null default 0 comment '数量',
    primary key (user_id_, name_)
);

alter table jspbb_user_restrict comment '用户限制表';

/*==============================================================*/
/* Table: jspbb_user_role                                       */
/*==============================================================*/
create table jspbb_user_role
(
    user_id_             bigint not null comment '用户ID',
    role_id_             bigint not null comment '角色表',
    primary key (user_id_, role_id_)
);

alter table jspbb_user_role comment '用户角色关联表';

alter table jspbb_access add constraint fk_access_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_access_ext add constraint fk_accessext_access foreign key (id_)
    references jspbb_access (id_) on delete restrict on update restrict;

alter table jspbb_action add constraint fk_action_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_answer add constraint fk_answer_question foreign key (question_id_)
    references jspbb_question (id_) on delete restrict on update restrict;

alter table jspbb_answer add constraint fk_answer_user_creator foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_answer add constraint fk_answer_user_editor foreign key (edit_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_answer_ext add constraint fk_reference_20 foreign key (id_)
    references jspbb_answer (id_) on delete restrict on update restrict;

alter table jspbb_answer_history add constraint fk_answerhistory_answer foreign key (answer_id_)
    references jspbb_answer (id_) on delete restrict on update restrict;

alter table jspbb_answer_history add constraint fk_answerhistory_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_attach add constraint fk_reference_23 foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_attach_ref add constraint fk_attachref_attach foreign key (attach_id_)
    references jspbb_attach (id_) on delete restrict on update restrict;

alter table jspbb_comment add constraint fk_comment_edit_user foreign key (edit_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_comment add constraint fk_comment_parent foreign key (parent_id_)
    references jspbb_comment (id_) on delete restrict on update restrict;

alter table jspbb_comment add constraint fk_comment_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_message add constraint fk_message_messagedetail foreign key (messagedetail_id_)
    references jspbb_message_detail (id_) on delete restrict on update restrict;

alter table jspbb_message add constraint fk_message_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_message add constraint fk_message_user_contact foreign key (contact_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_message add constraint fk_message_user_from foreign key (from_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_message add constraint fk_message_user_to foreign key (to_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_notification add constraint fk_notification_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_question add constraint fk_question_user_active foreign key (active_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_question add constraint fk_question_user_creator foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_question add constraint fk_question_user_editor foreign key (edit_user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_question_ext add constraint fk_questionext_question foreign key (id_)
    references jspbb_question (id_) on delete restrict on update restrict;

alter table jspbb_question_history add constraint fk_questionhistory_question foreign key (question_id_)
    references jspbb_question (id_) on delete restrict on update restrict;

alter table jspbb_question_history add constraint fk_questionhistory_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_question_tag add constraint fk_questiontag_question foreign key (question_id_)
    references jspbb_question (id_) on delete restrict on update restrict;

alter table jspbb_question_tag add constraint fk_questiontag_tag foreign key (tag_id_)
    references jspbb_tag (id_) on delete restrict on update restrict;

alter table jspbb_user add constraint fk_user_group foreign key (group_id_)
    references jspbb_group (id_) on delete restrict on update restrict;

alter table jspbb_user_ext add constraint fk_userext_user foreign key (id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_user_openid add constraint fk_useropenid_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_user_restrict add constraint fk_userrestrict_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;

alter table jspbb_user_role add constraint fk_userrole_role foreign key (role_id_)
    references jspbb_role (id_) on delete restrict on update restrict;

alter table jspbb_user_role add constraint fk_userrole_user foreign key (user_id_)
    references jspbb_user (id_) on delete restrict on update restrict;