package com.cyz.basic.valid.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.Assert;

import com.cyz.basic.util.PackageUtil;
import com.cyz.basic.util.StrUtil;
import com.cyz.basic.valid.annotation.EnableCheckOut;
import com.cyz.basic.valid.annotation.FieldMeta;

public final class ValidService {
public final static int VALIDFORONE = 1; //if one filed is checkout fail， break
	
	public final static int VALIDALL = 2;// checkout all fields
	
	private final static Map<Class<?>, Map<String, ValidMessage>> mapper = new HashMap<Class<?>, Map<String,ValidMessage>>();
	
	private String scanPackages = "com.cyz.ob.*.pojo";
	
	private PackageUtil util = PackageUtil.instance();
	
	
    /**
     * 
     */
	public ValidService () {
		
		Set<Class<?>> clss = util.findClassAnnotation(this.scanPackages, EnableCheckOut.class);
		for (Class<?> c : clss) {
			Field[] fs = c.getDeclaredFields();
			Map<String, ValidMessage> vs = new HashMap<>();
			for(int i = 0; i < fs.length; i++) {
				try {
					String fieldName = fs[i].getName();
					FieldMeta meta = fs[i].getAnnotation(FieldMeta.class);
					if (meta == null) {
						continue;
					}				
					Method get = getMethod(fs[i], c);
					//如果没有get方法，表示不是bean属性,不可使用
					Assert.notNull(get, "属性：" + fieldName + " 请加上get set 方法");			
					Assert.isTrue(get.getReturnType() == fs[i].getType(), "get方法返回值类型必须与属性类型相同");
					vs.put(fieldName, new ValidMessage(fs[i], get, meta));
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new RuntimeException();
				}
			}
			mapper.put(c, vs);
		}
		
	}
	
	/**
	 * 
	 * @param field
	 * @param cls the class which is contaion field
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Method getMethod(Field field, Class<?> cls) throws NoSuchMethodException, SecurityException {
		
		String name = field.getName();
		
		String getMethodName = util.getMethodName(name);
		
		return cls.getDeclaredMethod(getMethodName);
		
	}
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public String validReturnFirstError(Object entity) {
		Map<String, List<String>> res = this.validFilterFields(entity, VALIDFORONE);
		Set<String> keyName = res.keySet();
		if (keyName == null || keyName.size() == 0) {
			return null;
		}
		for (String name : keyName) {
			List<String> l = res.get(name);
			for (String mess : l) {
				return name + "," + mess;
			}
		}
		return null;
	}
	
	/**
	 * This class is used to validate all attributes
	 * @param entity
	 * @return
	 */
	public Map<String, List<String>> validFields(Object entity, int validType) {
		return this.validFilterFields(entity, validType);
	}
	
	/**
	 * 
	 * @param entity
	 * @param fields the attributes you want to filter
	 * @return
	 */
	public Map<String, List<String>> validFilterFields(Object entity, int validType, String ...fields) {
		return this.validField(entity, true, validType, fields);
	}
	
	/**
	 * 
	 * @param entity
	 * @param fields  the attributes you want to checkout
	 * @return
	 */
	public Map<String, List<String>> validAcceptFields(Object entity, int validType, String ...fields) {
		return this.validField(entity, false, validType, fields);
	}
	
	/**
	 * valid the field about is bigger or is smaller, or is null ?
	 * @param entity
	 * @param type  if type is true, the method will filter the field which accept, otherwise, 他和method will rtain the field which accept
	 * @param fields
	 * @param validType 1-one 2-all
	 * @return
	 */
	public Map<String, List<String>> validField(Object entity, boolean type, int validType, String ...fields) {
		
		Map<String, List<String>> mapResult = new HashMap<>();
		if (entity == null) {
			return mapResult;
		}
		Map<String, ValidMessage> clsMapper = mapper.get(entity.getClass());
		Assert.notNull(clsMapper, "当前类没有加入校验中");
		Set<String> fieldNames = handleFileds(clsMapper.keySet(), type, fields);
		ValidMessage valid = null;
		FieldMeta meta = null;
		for (String name : fieldNames) {
			valid = clsMapper.get(name);
			Assert.notNull(valid, "当前属性没有加入校验中");
			Object val = null;
			try {
				val = valid.getMethod.invoke(entity);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				continue;
			}
			
			// 判断是否需要校验结束 ======
			
			List<String> errMessage = new LinkedList<>();
			meta = valid.meta;			
			mapResult.put(meta.key().equals("") ? name : meta.key(), errMessage);		
			int typeNum = switchType(valid.type); //值类型
			//判断是否开启非空校验
			if (meta.notNull() && (val == null || (typeNum == 1 && val.toString().trim().equals("")) )) {
				errMessage.add(meta.value() + "不能为空");
				//if the value is null and it open the not null checkout, that you need to checkout latter valid
				continue;
			}
			//检验最大值
			if (StrUtil.isNotEmpty(meta.max())) {
			    boolean maxResult = false;
				String maxResultMess = null;
				switch (typeNum) {
				case 1:
					maxResult = validMax(val, meta.max(), 1);
					maxResultMess = meta.value()+"最大长度是" + meta.max() + "字";
					break;
				case 2:
					maxResult = validMax(val, meta.max(), 2);
					maxResultMess = meta.value()+"最大值不能超过" + meta.max();
					break;
			    }
				Assert.notNull(maxResult, "属性：" + name + "的最大值标识含有非数字或非小数点的符号");
				if (!maxResult) {
					String res = "".equals(meta.commonMess()) ? ("".equals(meta.maxMess()) ? maxResultMess : meta.maxMess()) : meta.commonMess();
					errMessage.add(res);
				}
			}
			//校验最小值
			if (StrUtil.isNotEmpty(meta.min())) {				
				boolean minResult = false;
				String minResultMess = null;
				switch (typeNum) {
				case 1:
					minResult = validMin(val, meta.min(), 1);
					minResultMess = meta.value()+"最小长度是" + meta.max() + "字" ;
					break;
				case 2:		
					minResult = validMin(val, meta.min(), 2);
					minResultMess = meta.value()+"最小值不能小于" + meta.max() ;
					break;
			    }
				Assert.notNull(minResult, "属性：" + name + "的最小值标识含有非数字或非小数点的符号");		
				if (!minResult) {
					String res = "".equals(meta.commonMess()) ? ("".equals(meta.minMess()) ? minResultMess : meta.minMess()) : meta.commonMess();
					errMessage.add(res);
				}
			}	
			
			
			if (validType == VALIDFORONE && errMessage.size() > 0) {
				break;
			}
			
		}
		
		return mapResult;
	}

	/**
	 * return the type'num of the value
	 * @param type
	 * @return
	 */
	private int switchType(String type) {
		switch (type) {
			case "String":
				return 1;
			case "int":case "Integer":case "double":
			case "Double":case "float":case "Float":
			case "Byte":
				return 2;
        }
		return 0;
	}
	
	/**
	 * 
	 * @param val
	 * @param maxStr
	 * @param type 1-string 2-number
	 * @return
	 */
	private Boolean validMax(Object val, String maxStr, int type) {
		
		if (maxStr.equals("")) {
			
			return true;
		}
		if (val == null) {
			return false;
		}
		if (!StrUtil.isNumeric(maxStr)) {
			return null;
		}
		
		Double max = Double.parseDouble(maxStr);
		switch (type) {
			case 1:
				return val.toString().length() <= max;
			case 2:
				return Double.parseDouble(val.toString()) <= max;
		}
		return false;
	}
	
    private Boolean validMin(Object val, String minStr, int type) {
		
		if (minStr.equals("")) {
			
			return true;
		}
		if (val == null) {
			return false;
		}
		if (!StrUtil.isNumeric(minStr)) {
			return null;
		}
		
		Double min = Double.parseDouble(minStr);
		switch (type) {
		case 1:
			return val.toString().length() >= min;
		case 2:
			return Double.parseDouble(val.toString()) >= min;
		}
		return false;
	}
	
	/**
	 * 
	 * @param fieldNames
	 * @param type
	 * @param fields
	 * @return
	 */
	private Set<String> handleFileds(Set<String> fieldNames, boolean type, String[] fields) {
		if (fields == null || fields.length == 0) {
			return fieldNames;
		}
        if (type) {
        	fieldNames.removeAll(Arrays.asList(fields));
        } else {
        	for (int i = 0; i < fields.length; i++) {
        		Assert.isTrue(fieldNames.contains(fields[i]), "属性：" + fields[i] + "不存在");
        	}
        	fieldNames.removeAll(Arrays.asList(fields));
        	Collections.addAll(fieldNames, fields);
        }
        return fieldNames;
	}

    /**
     * 
     * @author cyz
     *
     */
	private class ValidMessage {
		
		String type;
		
		Field field;
		
	    Method getMethod;
		
		FieldMeta meta;
		
		ValidMessage (Field field, Method getMethod, FieldMeta meta) {
			this.field = field;
			this.getMethod = getMethod;
			this.meta = meta;
			this.type = this.field.getType().getSimpleName();
		}
	
	}
}
