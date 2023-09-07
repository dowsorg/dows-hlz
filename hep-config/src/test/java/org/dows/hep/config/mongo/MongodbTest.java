package org.dows.hep.config.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.regex.Pattern;

@SpringBootTest(classes = HepApplication.class)
class MongodbTest {

    // 注入mongoTemplate
    @Autowired
    private MongoTemplate mongoTemplate;

    /*
    常用方法
mongoTemplate.findAll(User.class): 查询User文档的全部数据
mongoTemplate.findById(<id>, User.class): 查询User文档id为id的数据
mongoTemplate.find(query, User.class);: 根据query内的查询条件查询
mongoTemplate.upsert(query, update, User.class): 修改
mongoTemplate.remove(query, User.class): 删除
mongoTemplate.insert(User): 新增
Query对象
1、创建一个query对象（用来封装所有条件对象)，再创建一个criteria对象（用来构建条件）
2、 精准条件：criteria.and(“key”).is(“条件”)
模糊条件：criteria.and(“key”).regex(“条件”)
3、封装条件：query.addCriteria(criteria)
4、大于（创建新的criteria）：Criteria gt = Criteria.where(“key”).gt（“条件”）
小于（创建新的criteria）：Criteria lt = Criteria.where(“key”).lt（“条件”）
5、Query.addCriteria(new Criteria().andOperator(gt,lt));
6、一个query中只能有一个andOperator()。其参数也可以是Criteria数组。
7、排序 ：query.with（new Sort(Sort.Direction.ASC, "age"). and(new Sort(Sort.Direction.DESC, "date")))

     */

    // 添加操作
    @Test
    public void insert() {
        User user = new User();
        user.setAge(18);
        user.setName("bc");
        user.setEmail("123@qq.com");
//        for (int i= 0;i<10;i++) {
        User insert = mongoTemplate.insert(user);
//        }
        System.out.println(insert);
    }

    // 查询操作
    @Test
    public void findAll() {
        List<User> all = mongoTemplate.findAll(User.class);
        System.out.println(all);
    }

    // 根据id查询
    @Test
    public void findById() {
        User byId = mongoTemplate.findById("648b2e3c509cb42dbf90f383", User.class);
        System.out.println(byId);
    }

    // 条件查询
    @Test
    public void findUserList() {
        // name=aaa and age=18
        Query query1 = new Query(Criteria.where("name").is("aaa").and("age").is(18));
        List<User> list = mongoTemplate.find(query1, User.class);
        System.out.println(list);
    }

    // 模糊查询
    @Test
    public void findLikeUserList() {
        // name like a
        String name = "a";
        // 拼接
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Query query = new Query(
                Criteria.where("name").regex(pattern));
        List<User> list = mongoTemplate.find(query, User.class);
        System.out.println(list);
    }

    // 分页查询
    @Test
    public void findPageUserList() {
        int pageNo = 1;
        int pageSize = 4;
        String name = "a";
//        // 拼接
        String regex = String.format("%s%s%s", "^.*", name, ".*$");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Query query = new Query(
                Criteria.where("name").regex(pattern));
        // 分页构建
        // 查询记录数
        long count = mongoTemplate.count(query, User.class);
        // 分页
        List<User> list = mongoTemplate.find(
                // 当前页-1 乘以每页记录数
                query.skip((pageNo - 1) * pageSize).limit(pageSize), User.class);
        System.out.println("记录数" + count);
        System.out.println("查询结果" + list);
    }

    // 修改
    @Test
    public void update() {
        // 根据id查询
        User user = mongoTemplate.findById("648b2e3c509cb42dbf90f383", User.class);
        // 设置修改值
        user.setName("asdf");
        user.setAge(100);
        user.setEmail("popop@!!!");
        // 调用方法实现修改
        Update update = new Update();
        update.set("name", user.getName());
        update.set("age", user.getAge());
        update.set("email", user.getEmail());
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        UpdateResult upsert = mongoTemplate.upsert(query, update, User.class);
        // getModifiedCount修改行数
        long modifiedCount = upsert.getModifiedCount();
        System.out.println(modifiedCount);
    }

    // 删除
    @Test
    public void delete() {
        Query query = new Query(Criteria.where("_id").is("648b2e3c509cb42dbf90f383"));
        DeleteResult remove = mongoTemplate.remove(query, User.class);
        long deletedCount = remove.getDeletedCount();
        System.out.println(deletedCount);
    }
}
