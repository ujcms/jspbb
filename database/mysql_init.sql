insert into jspbb_group (id_,name_,type_,question_max_,answer_max_,comment_max_,message_max_,upload_max_) values (1,'管理员',1,999,999,999,999,999);
insert into jspbb_group (id_,name_,type_,is_trusted_) values (10,'0级',2,0);
insert into jspbb_group (id_,name_,type_,reputation,question_max_,answer_max_,comment_max_,message_max_,upload_max_) values (11,'1级',2,1,2,5,10,30,15);
insert into jspbb_seq (name_, next_val_) values ('jspbb_group', 12);
insert into jspbb_user (id_,group_id_,username_,created_) values (1,1,'admin',now());
insert into jspbb_user_ext (id_,password_,salt_,login_date_) values (1,'aca2d6bd777ac00e4581911a87dcc8a11b5faf11e08f584513e380a01693ef38','123456',now());
insert into jspbb_seq (name_, next_val_) values ('jspbb_user', 2);