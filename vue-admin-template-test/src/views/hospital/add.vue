<template>
  <div class="app-container">
    医院设置添加
    <el-form label-width="120px">
         <el-form-item label="医院名称">
            <el-input v-model="hospitalSet.hosname"/>
         </el-form-item>
         <el-form-item label="医院编号">
            <el-input v-model="hospitalSet.hoscode"/>
         </el-form-item>
         <el-form-item label="api基础路径">
            <el-input v-model="hospitalSet.apiUrl"/>
         </el-form-item>
         <el-form-item label="联系人姓名">
            <el-input v-model="hospitalSet.contactsName"/>
         </el-form-item>
         <el-form-item label="联系人手机">
            <el-input v-model="hospitalSet.contactsPhone"/>
         </el-form-item>
         <el-form-item>
            <el-button type="primary" @click="saveOrUpdate">保存</el-button>
         </el-form-item>
      </el-form>



  </div>
</template>

<script>
    import hospSet from '@/api/hospitalSet';
export default {
  data(){
    return{
      hospitalSet:{}
    }
  },
  created(){
    //获取路由里面的id
    //查询医院信息
    if(this.$route.params && this.$route.params.id){
      
      const id = this.$route.params.id
      this.getHospitalSet(id)
    }else{
      this.hospitalSet={}
    }
  },
  methods:{

    //根据id查询医院
    getHospitalSet(id){
      hospSet.getHospSet(id)
      .then(response => {

        console.log("yes",response.data)
        this.hospitalSet = response.data
      })
      .catch(error =>{
        console.log(error)
      })
    },

    save(){
      hospSet.saveHospital(this.hospitalSet)
      .then(response => {
        this.$message({
              type: 'success',
              message: '添加成功!',
              
            })
        //路由跳转
        this.$router.push({path:'/hospSet/list'})
      })
    },
    update(){
      hospSet.updateHospital(this.hospitalSet)
      .then(response => {
        this.$message({
              type: 'success',
              message: '修改成功!',
              
            })
        //路由跳转
        this.$router.push({path:'/hospSet/list'})
      })
    },
    saveOrUpdate(){
      if(!this.hospitalSet.id){
        this.save()
      }else{
        this.update()
      }
    }
  }
}
</script>

<style>

</style>