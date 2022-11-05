<template>
 <div class="app-container">
    <el-form :inline="true" class="demo-form-inline">
   <el-form-item>
      <el-input  v-model="searchObj.hosname" placeholder="医院名称"/>
   </el-form-item>
   <el-form-item>
      <el-input v-model="searchObj.hoscode" placeholder="医院编号"/>
   </el-form-item>
   <el-button type="primary" icon="el-icon-search" @click="getList()">查询</el-button>
</el-form>
<div>
   <el-button type="danger" size="mini" @click="removeRows()">批量删除</el-button>
</div>

<!-- banner列表 -->
    <el-table
        :data="list" stripe style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"/>

        <el-table-column type="index" width="50" label="序号"/>
        <el-table-column prop="hosname" label="医院名称"/>
        <el-table-column prop="hoscode" label="医院编号"/>
        <el-table-column prop="apiUrl" label="api基础路径" width="200"/>
        <el-table-column prop="contactsName" label="联系人姓名"/>
        <el-table-column prop="contactsPhone" label="联系人手机"/>
        <el-table-column label="状态" width="80">
            <template slot-scope="scope">
                    {{ scope.row.status === 1 ? '可用' : '不可用' }}
            </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center">
            <template slot-scope="scope">
                <el-button type="danger" size="mini" 
                    icon="el-icon-delete" @click="removeDataById(scope.row.id)">删除 </el-button>

                <el-button type="danger" size="mini" v-show="scope.row.status == 1"
                    icon="el-icon-delete" @click="lockHospital(scope.row.id,0)">锁定 </el-button>
                    
                <el-button type="danger" size="mini" v-show="scope.row.status == 0"
                    icon="el-icon-delete" @click="lockHospital(scope.row.id,1)">取消锁定 </el-button>

                <router-link :to="'/hospSet/edit/'+scope.row.id">
                    <el-button type="primary" size="mini" icon="el-icon-edit"></el-button>
                </router-link>

            
            </template>
        </el-table-column>

</el-table>
<el-pagination
  :current-page="page"
  :page-size="limit"
  :total="total"
  style="padding: 30px 0; text-align: center;"
  layout="total, prev, pager, next, jumper"
  @current-change="getList"
/>

</div>

</template>

<script>
    import hospitalSet from '@/api/hospitalSet';
export default {
    data(){
        return{
            current:1,
            page:1,
            limit:3,
            searchObj:{},
            total:0,
            multipleSelection: [],
            list:[] //每页数据集合
        }
    },
    created(){
       this.getList()
    },
    methods:{
        getList(page = 1){
            this.current = page
            hospitalSet.getHospitalSetList(this.current,this.limit,this.searchObj)
            .then(response => { //参数是返回数据
                console.log(response)
                this.list = response.data.records
                this.total = response.data.total
            })
            .catch(error => {
                console.log(error)
            })
        },
        //删除
        removeDataById(id){
            // alert(id)

        this.$confirm('此操作将永久删除该医院信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => { //确定执行这里

            hospitalSet.deleteHospitalSet(id)
            .then(response =>{
                //提示
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                });
                //刷新数据
                this.getList(1)
            })

            })

        },
        //批量删除
        removeRows(){

            this.$confirm('此操作将永久删除该医院信息, 是否继续?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => { //确定执行这里
            var idList = []
            this.multipleSelection.forEach(item => {
                idList.push(item.id)
                
            })
            console.log(idList)
            hospitalSet.batchHospital(idList)
            .then(response =>{
                //提示
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                });
                //刷新数据
                this.getList(1)
            })
            .catch(error => {
                console.log(error)
            })

            })

        },
        handleSelectionChange(selection){
            // console.log(selection)
            this.multipleSelection = selection
        },
        //锁定和取消锁定
        lockHospital(id,status){
            hospitalSet.lockHospital(id,status)
            .then(response => {
                this.getList(this.current)
            })
        }
    }

}
</script>

<style>

</style>