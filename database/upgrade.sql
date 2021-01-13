/* 2021-01-12 1.0.0-beta 升级 1.0.0  */
alter table jspbb_question_ext add sensitive_words_ varchar(2000) comment '敏感词';
alter table jspbb_answer_ext add sensitive_words_ varchar(2000) comment '敏感词';
alter table jspbb_comment add sensitive_words_ varchar(2000) comment '敏感词';