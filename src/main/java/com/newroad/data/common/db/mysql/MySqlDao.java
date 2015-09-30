package com.newroad.data.common.db.mysql;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlDao {

  private static final Logger logger = LoggerFactory.getLogger(MySqlDao.class);

  private SqlSessionFactory sqlSessionFactory;

  public MySqlDao() {
    this.sqlSessionFactory = MySqlConnectionFactory.getSqlSessionFactory();
  }

  public Object selectOne(String sqlId, Object para) {
    Object obj = null;
    SqlSession session = sqlSessionFactory.openSession();
    try {
      obj = session.selectOne(sqlId, para);
      session.commit();
    } catch (Exception e) {
      session.rollback();
      logger.error("MySqlDao selectOne Exception:", e);
    } finally {
      session.close();
    }
    return obj;
  }

  public List<?> selectList(String sqlId, Object para, int offset, int limit) {
    List<?> list = null;
    SqlSession session = sqlSessionFactory.openSession();
    try {
      list = session.selectList(sqlId, para, new RowBounds(offset, limit));
      session.commit();
    } catch (Exception e) {
      session.rollback();
      logger.error("MySqlDao selectList Exception:", e);
    } finally {
      session.close();
    }
    return list;
  }

  public int insert(String sqlId, Object para) {
    int insert = 0;
    SqlSession session = sqlSessionFactory.openSession();
    try {
      insert = session.insert(sqlId, para);
      session.commit();
    } catch (Exception e) {
      session.rollback();
      logger.error("MySqlDao insert Exception:", e);
    } finally {
      session.close();
    }
    return insert;
  }

  public int update(String sqlId, Object para) {
    int update = 0;
    SqlSession session = sqlSessionFactory.openSession();
    try {
      update = session.update(sqlId, para);
      session.commit();
    } catch (Exception e) {
      session.rollback();
      logger.error("MySqlDao update Exception:", e);
    } finally {
      session.close();
    }
    return update;
  }

  public int delete(String sqlId, Object para) {
    int delete = 0;
    SqlSession session = sqlSessionFactory.openSession();
    try {
      delete = session.delete(sqlId, para);
      session.commit();
    } catch (Exception e) {
      session.rollback();
      logger.error("MySqlDao delete Exception:", e);
    } finally {
      session.close();
    }
    return delete;
  }

  public SqlSession openSession() {
    SqlSession session = sqlSessionFactory.openSession();
    return session;
  }

  public void closeSession(SqlSession session) {
    session.close();
  }
}
