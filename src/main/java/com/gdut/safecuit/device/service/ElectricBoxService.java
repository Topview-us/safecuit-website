package com.gdut.safecuit.device.service;

import com.gdut.safecuit.common.Page;
import com.gdut.safecuit.common.UniqueMainKeyMapper;
import com.gdut.safecuit.common.base.BaseDao;
import com.gdut.safecuit.common.base.BaseServiceImpl;
import com.gdut.safecuit.device.common.po.ElectricBox;
import com.gdut.safecuit.device.common.po.ElectricUser;
import com.gdut.safecuit.device.common.vo.ElectricBoxRelateUserVO;
import com.gdut.safecuit.device.dao.ElectricBoxMapper;
import com.gdut.safecuit.device.dao.ElectricUserMapper;
import com.gdut.safecuit.user.common.po.User;
import com.gdut.safecuit.user.dao.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Garson in 15:35 2018/1/19
 * Description :
 */
@Service
public class ElectricBoxService extends BaseServiceImpl<ElectricBox> {

	@Resource
	private ElectricBoxMapper electricBoxMapper;
	@Resource
	private ElectricUserMapper electricUserMapper;
	@Resource
	private UniqueMainKeyMapper uniqueMainKeyMapper;
	@Resource
	private UserMapper userMapper;

	/**
	 * 添加电箱
	 * @param electricBox 添加的电箱对象，属性包括电箱名称name、地址address、电箱所属机构的id、经度longitude、纬度latitude
	 * @return 添加数据的条数
	 */
	@Transactional
	public int insertElectricBox(ElectricBox electricBox){
		Integer id = uniqueMainKeyMapper.getMainKey();
		electricBox.setId(id+1);
		uniqueMainKeyMapper.updateMainKey(id+1 ,id);

		electricBox.setDelTag(0);
		//添加电箱
		return electricBoxMapper.insertSelective(electricBox);

	}

	//关联管理员
	public int relateUser(Integer electricBoxId ,List<Integer> userIds){
		//if (electricBoxMapper.hasRelatedUser(electricBoxId ,))
		//判断该电箱是否已关联选择的用户
		for (Integer userId : userIds) {
			if (electricUserMapper.hasRelatedUser(electricBoxId ,userId) != null)
				return -3;
		}
		return electricUserMapper.relateUser(electricBoxId ,userIds ,new Date());
	}

	//删除关联的管理员
	public int deleteRelateUser(Integer electricBoxId ,Integer userId){
		return electricUserMapper.deleteRelateUser(electricBoxId ,userId);
	}

	//显示关联管理员
	public List<ElectricBoxRelateUserVO> showRelatedUser(Page page , Integer electricBoxId){
		List<ElectricUser> electricUsers = electricUserMapper.selectByElectricBoxId(electricBoxId ,page);
		List<ElectricBoxRelateUserVO> electricBoxRelateUserVOS = new ArrayList<>();

		for (ElectricUser electricUser : electricUsers) {
			User user = userMapper.selectByPrimaryKey(electricUser.getUserId());
			if (user == null)
				continue;
			ElectricBoxRelateUserVO electricBoxRelateUserVO = new ElectricBoxRelateUserVO();
			electricBoxRelateUserVO.setUserId(user.getUserId());
			electricBoxRelateUserVO.setUserRealName(user.getRealName());
			electricBoxRelateUserVO.setPhone(user.getPhone());
			electricBoxRelateUserVO.setDate(electricUser.getDate());
			electricBoxRelateUserVOS.add(electricBoxRelateUserVO);
		}
		return electricBoxRelateUserVOS;
	}

	public Integer getRelatedUserTotal(Integer electricBoxId){
		return electricUserMapper.getRelatedUserTotal(electricBoxId);
	}

	/**
	 * 删除电箱
	 * @param id 电箱id
	 * @return 删除数
	 */
	public int deleteElectricBoxById(Integer id){
		return electricBoxMapper.deleteByPrimaryKey(id);
	}

	/**
	 * 假删除
	 * @param id 电箱id
	 * @return 修改数据条数
	 */
	public int fakeDeleteElectricBox(Integer id){
		return electricBoxMapper.fakeDelete(id);
	}

	/**
	 * 分页搜索、显示某机构下的电箱
	 * @param pageNo 当前页数
	 * @param pageSize 页数数据量
	 * @param orgId 电箱所属的机构id
	 * @param name 电箱的名称
	 * @return ElectricBoxVO集合
	 */
	/*public List<ElectricBoxVO> selectElectricBoxByPage(int pageNo , int pageSize , int orgId ,String name){
		Page page;

		if(cacheMap.get("ELECTRIC_BOX_TOTAL") == null)
			cacheMap.put("ELECTRIC_BOX_TOTAL" ,electricBoxMapper.getTotal());
		//获取电箱总数缓存
		Integer  electric_box_total = (Integer) cacheMap.get("ELECTRIC_BOX_TOTAL");

		page = new Page(pageSize,pageNo,electric_box_total);
		//搜索对应机构的电箱
		List<ElectricBox> electricBoxes = electricBoxMapper.selectByPage(page ,orgId ,name);

		List<ElectricBoxVO> electricBoxVOS = new ArrayList<>();

		for (ElectricBox electricBox:electricBoxes) {

			if(electricBox.getDelTag() == 1)
				continue;

			//搜索电箱对应机构的信息
			Map<String ,String> map1 = electricBoxMapper.searchOrgInfo(electricBox.getOrgId());

			ElectricBoxVO electricBoxVO = new ElectricBoxVO(electricBox.getId() ,electricBox.getName() ,map1.get("address")+electricBox.getAddress()
					,electricBox.getLongitude() ,electricBox.getLatitude() ,map1.get("orgName") ,map1.get("personName")
					,map1.get("phone"));

			electricBoxVOS.add(electricBoxVO);
		}

		return electricBoxVOS;
	}*/

	/**
	 * 修改电箱
	 * @param electricBox 修改的电箱对象，属性包括：电箱名称name、地址address、电箱所属机构的id、经度longitude、纬度latitude
	 * @return 修改数据条数
	 */
	@Transactional
	public int updateElectricBox(ElectricBox electricBox){

		return electricBoxMapper.updateByPrimaryKeySelective(electricBox);

		// 如果该设备假删除标识为1，则返回0
		/*if(electricBoxMapper.selectDelTag(electricBox.getId()) == 1)
			return 0;
		else {
			update = electricBoxMapper.updateByPrimaryKeySelective(electricBox);
		}
		return update;*/
	}

	@Override
	public BaseDao<ElectricBox> getDao() {
		return electricBoxMapper;
	}
}
