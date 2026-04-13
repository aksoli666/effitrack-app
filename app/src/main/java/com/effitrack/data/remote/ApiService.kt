package com.effitrack.data.remote

import com.effitrack.data.model.Equipment
import com.effitrack.data.model.EquipmentStatusUpdate
import com.effitrack.data.model.LoginRequest
import com.effitrack.data.model.Task
import com.effitrack.data.model.TaskCompleteRequest
import com.effitrack.data.model.TaskUpdateRequest
import com.effitrack.data.model.User
import com.effitrack.util.Constants.PARAM_ID
import com.effitrack.util.Constants.PARAM_INV
import com.effitrack.util.Constants.PARAM_TASK_ID
import com.effitrack.util.Constants.PARAM_USER_ID
import com.effitrack.util.Constants.URL_AUTH_LOGIN
import com.effitrack.util.Constants.URL_EQUIPMENT_BY_ID
import com.effitrack.util.Constants.URL_EQUIPMENT_SEARCH
import com.effitrack.util.Constants.URL_EQUIPMENT_STATUS
import com.effitrack.util.Constants.URL_REPORTS_SEND
import com.effitrack.util.Constants.URL_TASKS_BY_USER
import com.effitrack.util.Constants.URL_TASKS_COMPLETE
import com.effitrack.util.Constants.URL_TASKS_START
import com.effitrack.util.Constants.URL_USERS_EQUIPMENT
import com.effitrack.util.Constants.URL_USERS_PROFILE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST(URL_AUTH_LOGIN)
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ResponseBody>

    @GET(URL_USERS_PROFILE)
    suspend fun getUserProfile(
        @Path(PARAM_ID) userId: Long
    ): Response<User>

    @GET(URL_USERS_EQUIPMENT)
    suspend fun getUserEquipment(
        @Path(PARAM_ID) userId: Long
    ): Response<List<Equipment>>

    @POST(URL_USERS_EQUIPMENT)
    suspend fun assignEquipmentToUser(
        @Path(PARAM_ID) userId: Long,
        @Query(PARAM_INV) inventoryNumber: String
    ): Response<ResponseBody>

    @GET(URL_EQUIPMENT_SEARCH)
    suspend fun findEquipmentByInv(
        @Query(PARAM_INV) inventoryNumber: String
    ): Response<Equipment>

    @GET(URL_EQUIPMENT_BY_ID)
    suspend fun getEquipmentById(
        @Path(PARAM_ID) equipmentId: Long
    ): Response<Equipment>

    @POST(URL_EQUIPMENT_STATUS)
    suspend fun updateEquipmentStatus(
        @Path(PARAM_ID) equipmentId: Long,
        @Body statusUpdate: EquipmentStatusUpdate
    ): Response<ResponseBody>

    @GET(URL_TASKS_BY_USER)
    suspend fun getUserTasks(
        @Path(PARAM_USER_ID) userId: Long
    ): Response<List<Task>>

    @POST(URL_TASKS_START)
    suspend fun startTask(
        @Path(PARAM_TASK_ID) taskId: Long
    ): Response<Task>

    @POST(URL_TASKS_COMPLETE)
    suspend fun completeTask(
        @Path(PARAM_TASK_ID) taskId: Long,
        @Body request: TaskCompleteRequest
    ): Response<Task>

    @POST(URL_REPORTS_SEND)
    suspend fun sendReport(
        @Path(PARAM_USER_ID) userId: Long
    ): Response<ResponseBody>

    @PUT("tasks/{id}")
    suspend fun updateTaskDetails(
        @Path("id") id: Long,
        @Body request: TaskUpdateRequest
    ): Response<Task>

    @POST("reports/equipment/{userId}")
    suspend fun sendEquipmentReport(@Path("userId") userId: Long): Response<ResponseBody>
}
