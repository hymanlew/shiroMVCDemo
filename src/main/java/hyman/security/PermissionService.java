package hyman.security;

import hyman.utils.Constant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

public class PermissionService {

    //@Resource
    //private BSysFunpermitMapper bSysFunpermitMapper;
    //@Resource
    //private BSysAppfunMapper bSysAppfunMapper;
    //
    //@Override
    //public Set<String> findPermissions(PfAcc pfAcc, HttpServletRequest request) {
    //    List<BSysFunpermit> permissionList = bSysFunpermitMapper.getPermitsByRelationId(pfAcc.getId(), 1,RedisTool.getPfId(request));
    //    List<BSysFunpermit> rolePermissionList = bSysFunpermitMapper.getRolePermits(pfAcc.getId(),RedisTool.getPfId(request), Constant.getProperty("appId", ""));
    //
    //    if (rolePermissionList != null && !rolePermissionList.isEmpty()) {
    //        if (null != permissionList) {
    //            permissionList.addAll(rolePermissionList);
    //        } else {
    //            permissionList = rolePermissionList;
    //        }
    //    }
    //    Set<String> permitsSet = new HashSet<String>();
    //    if (null != permissionList && !permissionList.isEmpty()) {
    //        for (BSysFunpermit bSysFunpermit : permissionList) {
    //            String funValue = "";
    //            String appfunId = bSysFunpermit.getAppfunId();
    //            BSysAppfun bSysFun = bSysAppfunMapper.selectByPrimaryKey(appfunId);
    //            List<Permit> permits = bSysFunpermit.getPermitList();
    //            if (permits == null) {
    //                continue;
    //            }
    //            if (bSysFun != null) {
    //                funValue = bSysFun.getAppfunValue()==null? bSysFun.getAppfunCode():bSysFun.getAppfunValue();
    //            }
    //            for (Permit permit : permits) {
    //                String code = permit.getCode();
    //                permitsSet.add(funValue + ":" + code);
    //            }
    //        }
    //        return permitsSet;
    //    }
    //    return new HashSet<String>();
    //}
    ///**
    // * 鏅鸿兘浜烘煡璇㈡暟鎹潈闄�
    // */
    //@Override
    //public Set<String> findPermissionsBySmart(PfAcc pfAcc, HttpServletRequest request) {
    //    List<BSysFunpermit> permissionList = bSysFunpermitMapper.getPermitsByRelationId(pfAcc.getId(), 1,RedisTool.getPfId(request));
    //    List<BSysFunpermit> rolePermissionList = bSysFunpermitMapper.getRolePermits(pfAcc.getId(),RedisTool.getPfId(request),Constant.getProperty("appId", ""));
    //
    //    if (rolePermissionList != null && !rolePermissionList.isEmpty()) {
    //        if (null != permissionList) {
    //            permissionList.addAll(rolePermissionList);
    //        } else {
    //            permissionList = rolePermissionList;
    //        }
    //    }
    //    Set<String> permitsSet = new HashSet<String>();
    //    if (null != permissionList && !permissionList.isEmpty()) {
    //        for (BSysFunpermit bSysFunpermit : permissionList) {
    //            String funValue = "";
    //            String appfunId = bSysFunpermit.getAppfunId();
    //            BSysAppfun bSysFun = bSysAppfunMapper.selectByPrimaryKey(appfunId);
    //            List<Permit> permits = bSysFunpermit.getPermitList();
    //            if (permits == null) {
    //                continue;
    //            }
    //            if (bSysFun != null) {
    //                funValue = bSysFun.getAppfunValue()==null? bSysFun.getAppfunCode():bSysFun.getAppfunValue();
    //            }
    //            for (Permit permit : permits) {
    //                String code = permit.getCode();
    //                permitsSet.add(funValue + ":" + code);
    //            }
    //        }
    //        return permitsSet;
    //    }
    //    return new HashSet<String>();
    //}
    ///**
    // * 鏅轰汉鏌ヨ鏁版嵁鏉冮檺
    // */
    //@Override
    //public Set<String> findPermissionsByOffice(BSysUser bSysUser, HttpServletRequest request) {
    //    List<BSysFunpermit> permissionList = bSysFunpermitMapper.getPermitsByRelationId(bSysUser.getUserId(), 1,RedisTool.getPfId(request));
    //    List<BSysFunpermit> rolePermissionList = bSysFunpermitMapper.getRolePermits(bSysUser.getUserId(),RedisTool.getPfId(request),Constant.getProperty("appId", ""));
    //
    //    if (rolePermissionList != null && !rolePermissionList.isEmpty()) {
    //        if (null != permissionList) {
    //            permissionList.addAll(rolePermissionList);
    //        } else {
    //            permissionList = rolePermissionList;
    //        }
    //    }
    //    Set<String> permitsSet = new HashSet<String>();
    //    if (null != permissionList && !permissionList.isEmpty()) {
    //        for (BSysFunpermit bSysFunpermit : permissionList) {
    //            String funValue = "";
    //            String appfunId = bSysFunpermit.getAppfunId();
    //            BSysAppfun bSysFun = bSysAppfunMapper.selectByPrimaryKey(appfunId);
    //            List<Permit> permits = bSysFunpermit.getPermitList();
    //            if (permits == null) {
    //                continue;
    //            }
    //            if (bSysFun != null) {
    //                funValue = bSysFun.getAppfunValue()==null? bSysFun.getAppfunCode():bSysFun.getAppfunValue();
    //            }
    //            for (Permit permit : permits) {
    //                String code = permit.getCode();
    //                permitsSet.add(funValue + ":" + code);
    //            }
    //        }
    //        return permitsSet;
    //    }
    //    return new HashSet<String>();
    //}
}
