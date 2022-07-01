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

    // 유저 정보
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

    // 유저의 게시물들
    public List<GetUserPostsRes> selectUserPosts(int userIdx){
        String selectUserPostsQuery =
                "SELECT p.postIdx as postIdx,\n" +
                        "       pi.imgUrl as postImgUrl\n" +
                        "FROM Post as p\n" +
                        "    join PostImgUrl as pi on pi.postIdx = p.postIdx and pi.status = 'ACTIVE'\n" +
                        "    join User as u on u.userIdx = p.userIdx\n" +
                        "WHERE p.status = 'ACTIVE' and u.userIdx = ?\n" +
                        "group by p.postIdx\n" +
                        "HAVING min(pi.postImgUrlIdx)\n" +
                        "order by p.postIdx desc;";
        int selectUserPostsParam = userIdx;
        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs,rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ), selectUserPostsParam);
    }

    // 유저 생성
    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (email,name,pwd,birth,nickName) values (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(),postUserReq.getName() ,postUserReq.getPwd(), postUserReq.getBirth(),postUserReq.getNickName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    // 이메일 확인
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

    // 프로필 변경
    public int updateProfile(int userIdx, PatchUserReq patchUserReq){
        String updateProfileQuery = "update User set profileImgUrl=?, name=?, nickName = ?, website=?, introduction=? where userIdx = ?";
        Object[] updateProfileParams = new Object[]{patchUserReq.getProfileImgUrl(), patchUserReq.getName(),
                patchUserReq.getNickName(), patchUserReq.getWebsite(), patchUserReq.getIntroduction(), userIdx};

        return this.jdbcTemplate.update(updateProfileQuery, updateProfileParams);
    }

    // 유저 삭제
    public int updateUserStatus(int userIdx){
        String deleteUserQuery = "update User set status='INACTIVE' where userIdx = ? ";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }

}
