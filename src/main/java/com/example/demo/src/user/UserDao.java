package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetUserInfoRes selectUserInfo(int userIdx){
        String selectUserInfoQuery = "SELECT User.userIdx,\n" +
                "       nickName,\n" +
                "       name,\n" +
                "       profileImgUrl,\n" +
                "       website,\n" +
                "       introduction,\n" +
                "       IF(postCount is null, 0, postCount) as postCount,\n" +
                "       IF(followerCount is null, 0, followerCount) as followerCount,\n" +
                "       IF(followingCount is null, 0, followingCount) as followingCount\n" +
                "FROM User\n" +
                "    left join (SELECT userIdx, COUNT(postIdx) as postCount\n" +
                "                FROM Post\n" +
                "                WHERE status = 'ACTIVE'\n" +
                "                group by userIdx) p on p.userIdx = User.userIdx\n" +
                "    left join (SELECT followerIdx, COUNT(followIdx) as followerCount\n" +
                "                FROM Follow\n" +
                "                WHERE status = 'ACTIVE'\n" +
                "                group by followerIdx) f1 on f1.followerIdx = User.userIdx\n" +
                "    left join (SELECT followeeIdx, COUNT(followIdx) as followingCount\n" +
                "                FROM Follow\n" +
                "                WHERE status = 'ACTIVE'\n" +
                "                group by followeeIdx) f2 on f2.followeeIdx = User.userIdx\n" +
                "WHERE User.userIdx = ? and status = 'ACTIVE';";
        int selectUserInfoParam = userIdx;
        return this.jdbcTemplate.queryForObject(selectUserInfoQuery,
                (rs,rowNum) -> new GetUserInfoRes(
                        rs.getString("nickName"),
                        rs.getString("name"),
                        rs.getString("profileImgUrl"),
                        rs.getString("website"),
                        rs.getString("introduction"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("postCount")
                ), selectUserInfoParam);
    }

    public List<GetUserPostsRes> selectUserPosts(int userIdx){
        String selectUserPostsQuery =
                " SELECT p.postIdx as postIdx,\n" +
                        "       pi.imgUrl as postImgUrl\n" +
                        "FROM Post as p\n" +
                        "    join PostImgUrl as pi on pi.postIdx = p.postIdx and pi.status = 'ACTIVE'\n" +
                        "    join User as u on u.userIdx = p.userIdx\n" +
                        "WHERE p.status = 'ACTIVE' and u.userIdx = ?\n" +
                        "order by p.postIdx;";
        int selectUserPostsParam = userIdx;
        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs,rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ), selectUserPostsParam);
    }

    public GetUserRes getUsersByEmail(String email){
        String getUsersByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByEmailParams);
    }


    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByIdxParams);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (email,name,pwd,birth,nickName) values (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(),postUserReq.getName() ,postUserReq.getPwd(), postUserReq.getBirth(),postUserReq.getNickName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    // 유저 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    // 회원 삭제
    public int updateUserStatus(int userIdx){
        String deleteUserQuery = "update User set status='INACTIVE' where userIdx = ? ";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }

}
