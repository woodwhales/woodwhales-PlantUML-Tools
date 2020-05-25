package org.woodwhales.plantuml.domain;

import static org.woodwhales.plantuml.constants.CommonConstant.BR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.woodwhales.plantuml.constants.CommonConstant;
import org.woodwhales.plantuml.constants.ProjectConstant;
import org.woodwhales.plantuml.util.StringTool;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ProjectNode {
	
	private String groupId;
	private String artifactId;
	private String version;
	private String packaging;
	private Boolean parentPom;
	
	private String filePathName;
	
	private JSONObject jsonObject;
	
	private DependencyNode parent;
	
	private String parentGroupId;
	
	private List<DependencyNode> dependencyManagement;
	
	private List<Module> modules;
	
	private List<DependencyNode> dependencies;
	
	private Map<String, String> parentProperties;
	
	private Map<String, String> properties;
	
	private String description;
	
	/**
	 * 获取当前项目节点的json字符串
	 * @return
	 */
	public String getJsonString() {
		return jsonObject.toString();
	}
	
	public String getPlantUML(HashMap<String, ProjectNode> map, Boolean isShowComponent) {
		StringBuffer sb = new StringBuffer("");
		if(this.isParentPom() && CollectionUtils.isNotEmpty(modules)) {
			
			sb.append(BR);
			
			sb.append(String.format("package \"%s\" {", artifactId+"-module"));
			sb.append(BR);
			
			this.modules.forEach(module -> {
				sb.append(String.format("    component \"%s\"", module.getModuleName())).append(BR);
			});
			
			sb.append("}");
			
			sb.append(BR);
			
			for (Module module : modules) {
				String childModuleFileName = StringUtils.join(filePathName, File.separator , module.getModuleName());
				FileReader fileReader;
				try {
					fileReader = new FileReader(childModuleFileName + File.separator + "pom.xml");
					JSONObject jsonObject = XML.toJSONObject(fileReader);
					JSONObject projectJsonObject = jsonObject.getJSONObject(ProjectConstant.PROJECT);
					ProjectNode childProjectNode = new ProjectNode(projectJsonObject, childModuleFileName);
					sb.append(childProjectNode.getPlantUML(map, isShowComponent));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			return sb.toString();
		}
		
		map.put(artifactId, this);
		if(isShowComponent) {
			sb.append(String.format("component [ %s ] as \"%s\"", artifactId, artifactId)).append(BR);
		}
		
		return sb.toString();
		
	}
	
	public ProjectNode(JSONObject jsonObject, String filePathName) {
		this.filePathName = filePathName;

		this.jsonObject = jsonObject;

		// 设置 artifactId
		this.artifactId = jsonObject.getString(ProjectConstant.ARTIFACTID);

		// 设置 parent
		initParent(jsonObject);
		
		// 设置 groupId
		initGroupId(jsonObject);
		
		// 设置 version
		initVersion(jsonObject);
		
		// 设置 description
		initDescription(jsonObject);
		
		// 设置 packaging
		initPackaging(jsonObject);
		
		// 设置 properties
		initProperties(jsonObject);
		
		// 设置 dependencies
		initDependencies(jsonObject);
		
		if(this.parentPom) {
			// 设置 modules
			initModules(jsonObject);
			
			// 设置 dependencyManagement
			initDependencyManagement(jsonObject);
		}
	}

	private void initPackaging(JSONObject jsonObject) {
		try {
			this.packaging = jsonObject.getString(ProjectConstant.PACKAGING);
		} catch (Exception e) {
			log.info("this module = {} not exist packaging", this.artifactId);
		}
		
		// 设置 parentPom，设置是否为父工程
		this.parentPom = ProjectConstant.POM.equals(this.packaging);
	}

	/**
	 * 设置 description
	 * @param jsonObject
	 */
	private void initDescription(JSONObject jsonObject) {
		try {
			this.description = jsonObject.getString(ProjectConstant.DESCRIPTION);
		} catch (Exception e) {
			log.info("this module = {} not exist description", this.artifactId);
		}
	}

	/**
	 * 设置 version
	 * @param jsonObject
	 */
	private void initVersion(JSONObject jsonObject) {
		try {
			this.version = jsonObject.getString(ProjectConstant.VERSION);
		} catch (Exception e) {
			log.info("this module = {} not exist version", this.artifactId);
		}
	}

	/**
	 * 设置 groupId
	 * @param jsonObject
	 */
	private void initGroupId(JSONObject jsonObject) {
		try {
			this.groupId = jsonObject.getString(ProjectConstant.GROUPID);
		} catch (Exception e) {
			this.groupId = this.parentGroupId;
			log.info("this module = {} not exist groupId, set groupId = {}", this.artifactId, this.parentGroupId);
		}
	}

	/**
	 * 设置 modules
	 * @param jsonObject
	 */
	private void initModules(JSONObject jsonObject) {
		try {
			JSONObject modulesJSONObject = jsonObject.getJSONObject(ProjectConstant.MODULES);
			try {
				JSONArray moduleJsonArray = modulesJSONObject.getJSONArray(ProjectConstant.MODULE);
				List<Module> modules = new ArrayList<>(moduleJsonArray.length());
				
				moduleJsonArray.forEach(module -> {
					modules.add(Module.parse(module.toString()));
				});
				
				this.modules = modules;
			} catch (Exception e) {
				List<Module> modules = Collections.singletonList(Module.parse(modulesJSONObject.getString(ProjectConstant.MODULE)));
				this.modules = modules;
			}
		} catch (Exception e) {
			log.info("this module = {} not exist modules", this.artifactId);
		}
	}

	/**
	 * 设置 dependencyManagement
	 * @param jsonObject
	 */
	private void initDependencyManagement(JSONObject jsonObject) {
		try {
			JSONObject dependenciesJSONObject = jsonObject.getJSONObject(ProjectConstant.DEPENDENCYMANAGEMENT)
					.getJSONObject(ProjectConstant.DEPENDENCIES);
			JSONArray dependenciesJsonArray = dependenciesJSONObject.getJSONArray(ProjectConstant.DEPENDENCY);
			this.dependencyManagement = convert(dependenciesJsonArray);
		} catch (Exception e) {
			log.info("this module = {} not exist dependencyManagement", this.artifactId);
		}
	}

	/**
	 * 设置 parent
	 * 如果没有 parent 节点，则设置 parentGroupId 和 groupId 值相同
	 * @param jsonObject
	 */
	private void initParent(JSONObject jsonObject) {
		try {
			this.parent = DependencyNode.parse(jsonObject.getJSONObject(ProjectConstant.PARENT));
			this.parentGroupId = this.parent.getGroupId();
		} catch (Exception e) {
			// 没有 parent 则设置 parentGroupId 为 groupId
			this.parentGroupId = this.groupId;
			log.info("this module = {} not exist parent, set parentGroupId = {}", this.artifactId, this.groupId);
		}
	}

	/**
	 * 设置 dependencies
	 * @param jsonObject
	 */
	private void initDependencies(JSONObject jsonObject) {
		try {
			JSONArray myDependenciesJsonArray = jsonObject.getJSONObject(ProjectConstant.DEPENDENCIES).getJSONArray(ProjectConstant.DEPENDENCY);
			this.dependencies = convert(myDependenciesJsonArray);
		} catch (Exception e) {
			log.info("this module = {} not exist dependencies", this.artifactId);
		}
	}

	/**
	 * 设置 properties
	 * @param jsonObject
	 */
	private void initProperties(JSONObject jsonObject) {
		try {
			JSONObject propertiesJsonObject = jsonObject.getJSONObject(ProjectConstant.PROPERTIES);
			Map<String, Object> propertiesMap = propertiesJsonObject.toMap();
			Set<String> keySet = propertiesJsonObject.keySet();
			Map<String, String> properties = new HashMap<>(keySet.size());
			keySet.stream().forEach(key -> {
				properties.put(key, propertiesMap.get(key).toString());
			});
			
			this.properties = properties;
		} catch (Exception e) {
			log.info("this module = {} not exist properties", this.artifactId);
		}
	}
	
	private List<DependencyNode> convert(JSONArray dependenciesJsonArray) {
		int dependenciesJsonArrayLength = dependenciesJsonArray.length();
		
		List<DependencyNode> dependencyNodes = new ArrayList<>(dependenciesJsonArrayLength);
		for (int index = 0; index < dependenciesJsonArrayLength; index++) {
			DependencyNode dependencyNode = DependencyNode.parse(dependenciesJsonArray.getJSONObject(index));
			String version = dependencyNode.getVersion();
			
			if(StringTool.isVersionKey(version)) {
				String versionKey = StringTool.getVersionKey(version);
				if(Objects.nonNull(this.properties) && this.properties.size() > 0) {
					dependencyNode.setVersion(this.properties.get(versionKey));
				}
				//TODO 本节点没有 properties，则获取父类的 properties
				
			}
			
			dependencyNodes.add(dependencyNode);
		}
		
		return dependencyNodes;
	}
	
	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getPackaging() {
		return packaging;
	}

	public List<DependencyNode> getDependencyManagement() {
		return dependencyManagement;
	}

	public List<DependencyNode> getDependencies() {
		return dependencies;
	}
	
	public Boolean isParentPom() {
		return parentPom;
	}
	
	public DependencyNode getParent() {
		return parent;
	}

	public List<Module> getModules() {
		return modules;
	}
	
	public List<String> getModuleNames() {
		return modules.stream().map(Module::getModuleName).collect(Collectors.toList());
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getFilePathName() {
		return filePathName;
	}
	
	/**
	 * 获取依赖模块名
	 * @param map 当前父工程中所有子模块列表
	 * @return
	 */
	public List<String> getRefrenceModuleNames(HashMap<String, ProjectNode> map) {
		
		if(isParentPom()) {
			return Collections.emptyList();
		}
		
		if(Objects.isNull(map) || map.size() == 0 || CollectionUtils.isEmpty(this.dependencies)) {
			return Collections.emptyList();
		}
		
		List<String> refrenceModuleNames = new ArrayList<>();
		for (DependencyNode dependency : dependencies) {
			String dependencyArtifactId = dependency.getArtifactId();
			String dependencyGroupId = dependency.getGroupId();
			ProjectNode projectNode = map.get(dependencyArtifactId);
			if(Objects.nonNull(projectNode) && this.groupId.equals(dependencyGroupId)) {
				refrenceModuleNames.add(dependencyArtifactId);
			}
		}
		
		return refrenceModuleNames;
	}

	public String getRefrencePlantUML(List<String> refrenceModuleNames) {
		StringBuffer sb = new StringBuffer("");
		
		if(CollectionUtils.isEmpty(refrenceModuleNames)) {
			return sb.toString();
		}
		
		for (String refrenceModuleName : refrenceModuleNames) {
			sb.append(String.format("[ %s ] -down-> [ %s ]", this.artifactId, refrenceModuleName)).append(CommonConstant.BR);
		}
		
		return sb.toString();
	}

	
}
