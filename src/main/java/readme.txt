Data Tranfer V2版程序上线流程
一.上线前准备工作
停止 Tomcat 现有应用

确保 MongoDB 现有lndb数据库复制集运行中(准备数据转换)

V1默认分类数据处理方案¶

二.系统及数据库切换上线
Application:

部署V1 application在生产环境的测试Server上

部署V2 application在生产环境的Server上

数据库切换
0.准备工作
其中:
将DB-LNT-03[172.17.120.103] 作为构建服务器;
PLUGIN-LNT-1[172.17.120.23] 作为应用服务器

创建DB-LNT-03和CACHE-3之间建立的SSH信任连接

1)源服务器上生成秘钥证书

  [bastion@DB-LNT-03 ~]$ sudo ssh-keygen -b 1024 -t rsa
  Generating public/private rsa key pair.
  Enter file in which to save the key (/root/.ssh/id_rsa): 
  Enter passphrase (empty for no passphrase):             <-- 直接输入回车
  Enter same passphrase again:                            <-- 直接输入回车
  Your identification has been saved in /root/.ssh/id_rsa.
  Your public key has been saved in /root/.ssh/id_rsa.pub.
  The key fingerprint is:
  f6:9d:2e:93:ec:0b:86:55:41:8f:10:9f:3a:17:11:d6 root@SLV-MT-Q
2)上传公钥证书文件到目标服务器
通过https://10.96.160.78/cgi-bh/control_connect.cgi登录APP-SLV-MTQ;
由于文件传输不能访问root目录,顾需要将/root/.ssh/id_rsa.pub复制到/home/bastion目录下

[bastion@B-LNT-03 ~]$ sudo cp /root/.ssh/id_rsa.pub /home/bastion/authorized_keys
下载/home/bastion/authorized_keys到本地,并分别上传到APP-LNT-1,APP-LNT-2的/home/bastion目录,然后移动到各自/root/.ssh目录下

[bastion@PLUGIN-LNT-1 ~]$ sudo cp /home/bastion/authorized_keys /root/.ssh
1.[DB-3 Server]初始化导出前一天mongo dump到测试mongoDB
停机10-20分钟

./mongodump --host 172.17.120.103 --port 30000 --username XXXX --password XXXX -d lndb --out /home/bastion/dataTransfer/mongoDump/mongodumpV1_1

[root@DB-LNT-03 backup]# sudo scp -P 22222 mongobackup201407012236.tar.gz root@172.17.120.23:/home/bastion/dataTransfer/mongoDump/       
2.[CACHE-3 Server]测试mongoDB上创建V2 lndbs数据库，基于现有复制集
./mongorestore --host 172.17.120.23 --port 10001 --username XXXX --password XXXX --objcheck -d lndb /home/bastion/dataTransfer/mongoDump/mongodumpV1_1/lndb/

./mongoShell.sh --port 30000
>use lndbs
>db.addUser("root","xxxxx")
>db.auth("root","xxxxx")
3.[CACHE-3 Server]测试mongoDB执行数据升级工具,生成V2 lndbs数据库data
java -Xmx30720m -Xms10240m -jar dataTransfer.jar >output.log 2>&1 &

4.[CACHE-3 Server]测试mongoDB中 lndbs 检查数据完整性
查看现有log信息
java -Xmx30720m -Xms10240m -jar dataIntegrity.jar >check.log 2>&1 &

查看ln_note/ln_category/ln_tag/ln_resource count

5.[CACHE-3 Server]备份V2 mongoDB lndbs
./mongodump --host 172.17.120.23 --port 10001 --username XXXX --password XXXX -d lndbs --out /home/bastion/dataTransfer/mongoDump/mongodumpV2_1
升级当天:

6.[DB-3 Server]备份当前生产环境下所有数据库数据
关闭现有定时cron服务
/sbin/service crond stop

/data/mongodb/script/logRotate.sh
/data/mongodb/script/backup.sh
7.[DB-3 Server]生成当前生产环境mongoDB lndb dump,并导出
Full dumps:
./mongodump --host 172.17.120.103 --port 30000 --username XXXX --password XXXX --oplog --out /home/bastion/dataTransfer/mongoDump/mongodumpV1_Full

lndb dump:
./mongodump --host 172.17.120.103 --port 30000 --username XXXX --password XXXX -d lndb --out /home/bastion/dataTransfer/mongoDump/mongodumpV1_2

sudo scp -P 22222 mongobackup201407012236.tar.gz root@172.17.120.23:/home/bastion/dataTransfer/mongoDump/  
8.[CACHE-3 Server]导入当前mongo dump到测试mongoDB
./mongorestore --host 172.17.120.23 --port 10001 --username XXXX --password XXXX --objcheck -d lndb /home/bastion/dataTransfer/mongoDump/mongodumpV1_2/lndb/
9.[CACHE-3 Server]测试mongoDB执行数据增量升级工具
查看最近一次的V1 oplog记录的sysTime

db.ln_oplog.enIndex({sysTime:1})
db.ln_oplog.find().sort({sysTime:-1})

java -Xmx30720m -Xms10240m -jar dataIncrement.jar >increment.log 2>&1 &

10.[CACHE-3 Server]测试mongoDB检查数据完整性
查看现有log信息
java -Xmx30720m -Xms10240m -jar dataIntegrity.jar >check.log 2>&1 &

查看ln_note/ln_category/ln_tag/ln_resource count

11.[CACHE-3 Server]为新数据库创建Index
MongoDB Index Setting:
1.Note Index
db.ln_note.ensureIndex({_id: 1}) (default)
db.ln_note.ensureIndex({status: 1,userID:1})

2.Resource Index
db.ln_resource.ensureIndex({_id: 1})
db.ln_resource.ensureIndex({clientResourceID:1})
db.ln_resource.ensureIndex({noteID:1})

3.Category Index
db.ln_category.ensureIndex({_id: 1})
db.ln_category.ensureIndex({categoryName: 1, userID:1})

4.Tag Index
db.ln_tag.ensureIndex({_id: 1})
db.ln_tag.ensureIndex({tagName: 1, userID:1})

5.User Index
db.ln_user.ensureIndex({_id: 1})
db.ln_user.ensureIndex({snsUserID: 1})

6.Oplog Index
db.ln_oplog.ensureIndex({_id: 1})
db.ln_oplog.ensureIndex({dataID:1})
db.ln_oplog.ensureIndex({userID:1})

7.Remove Index
db.ln_collection.dropIndex({_id:1})
12.[CACHE-3 Server]导出测试mongoDB lndbs dump
tar czvf /data/mongodb/backup/mongodump3

./mongodump --host 172.17.120.23 --port 10001 --username XXXX --password XXXX -d lndbs --out /home/bastion/dataTransfer/mongoDump/mongodumpV2_2
13.[DB-3 Server]导入lndbs 到生产环境数据库
sudo scp -P 22222 root@172.17.120.23:/home/bastion/dataTransfer/mongoDump/mongobackup201407012236.tar.gz /home/bastion/dataTransfer/mongoDump/ 

>use lndbs
>db.addUser("xxxx","xxxx")
>db.auth("xxxx","xxxx")

./mongorestore --host 172.17.120.103 --port 30000 --username XXXX --password XXXX --objcheck -d lndbs /home/bastion/dataTransfer/mongoDump/mongodumpV2_2/lndbs/
其他：文件数据云端矫正

矫正服务器端与COS云端不一致数据

关闭现有COS Callback接口
三.上线后验证工作
1.对比V1和V2 MongoDB数据库是否正确

2.Web application 是否可以访问

3.终端是否可以同步成功

4.PC端是否可以同步成功

5.查看Server端log是否包含错误信息

6.使用测试账号测试

7. 2.0 APP+新用户注册同步
2.0 APP+老用户同步
3.0 APP+新用户注册同步（看默认分类）
3.0 APP+老用户同步