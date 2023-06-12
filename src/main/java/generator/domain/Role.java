package generator.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

/**
* 
* @TableName role
*/
public class Role implements Serializable {

    /**
    * 
    */
    @NotNull(message="[]不能为空")
    @ApiModelProperty("")
    private Integer roleId;
    /**
    * 
    */
    @Size(max= 10,message="编码长度不能超过10")
    @ApiModelProperty("")
    @Length(max= 10,message="编码长度不能超过10")
    private String roleName;
    /**
    * 
    */
    @ApiModelProperty("")
    private Integer rolePermission;
    /**
    * 
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("")
    @Length(max= 50,message="编码长度不能超过50")
    private String roleDescription;

    /**
    * 
    */
    private void setRoleId(Integer roleId){
    this.roleId = roleId;
    }

    /**
    * 
    */
    private void setRoleName(String roleName){
    this.roleName = roleName;
    }

    /**
    * 
    */
    private void setRolePermission(Integer rolePermission){
    this.rolePermission = rolePermission;
    }

    /**
    * 
    */
    private void setRoleDescription(String roleDescription){
    this.roleDescription = roleDescription;
    }


    /**
    * 
    */
    private Integer getRoleId(){
    return this.roleId;
    }

    /**
    * 
    */
    private String getRoleName(){
    return this.roleName;
    }

    /**
    * 
    */
    private Integer getRolePermission(){
    return this.rolePermission;
    }

    /**
    * 
    */
    private String getRoleDescription(){
    return this.roleDescription;
    }

}
