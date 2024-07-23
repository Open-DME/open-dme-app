package dto

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.patch
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Resource("/api/v1")
class Api() {
    @Resource("/test")
    class Test(val parent: Api = Api())
}

@Resource("/member")
class Member() {
    @Resource("/{memberId}/status")
    class Status(val parent: Member = Member(), val memberId: Long)

    @Resource("/member/{memberId}/awayUntil")
    class Away(val parent: Member = Member(), val memberId: Long)
}

@Resource("/department")
class Department {}

@Resource("/call")
class Call


enum class Status {
    DISPATCHED, AVAILABLE, WITH_DELAY, AWAY
}


object Responses {
    @Serializable
    data class Test(
        val name: String,
        val roles: List<String>
    )


    @Serializable
    data class MemberSkill(
        val id: Long?,
        val member: Member,
        val skill: Skill
    )

    @Serializable
    data class Skill(
        val id: Long?,
        val member: Member
    )

    @Serializable
    data class Department(
        val id: Long?,
        val name: String,
        val admin: Member
    )

    @Serializable
    data class Member(
        val id: Long?,
        val department: Department?,
        val name: String,
        val skills: List<MemberSkill>,
        val email: String,
        val status: Status,
        val awayUntil: LocalDateTime
    )

    @Serializable
    data class Call(
        val id: Long,
        val createdAt: LocalDateTime,
        val department: Department,
        val vehicles: List<Vehicle>
    )

    @Serializable
    data class Vehicle(
        val id: Long,
        val name: String,
        val seats: Int,
        val department: Department
    )

}

object Requests {
    @Serializable
    data class Member(
        val name: String,
        val email: String,
        val departmentId: Long? = null
    )

    @Serializable
    data class Department(
        val name: String,
        val adminId: Long
    )

    @Serializable
    data class Call(
        val departmentId: Long,
        val vehicleIds: List<Long>
    )
}


class ApiService(private val client: HttpClient) {

    suspend fun test(
        onSuccess: (data: Responses.Test) -> Unit,
        onError: (status: HttpStatusCode, body: String) -> Unit
    ) {
        val response = client.post(Api.Test())
        if (response.status.value in 200..299) {
            onSuccess(response.body())
        } else {
            onError(response.status, response.bodyAsText())
        }
    }

    suspend fun createMember(member: Requests.Member): Responses.Member {
        return client.post(Member()) {
            setBody(member)
        }.body()
    }

    suspend fun updateStatus(memberId: Long, status: Status): Boolean {
        return client.patch(Member.Status(memberId = memberId)) {
            setBody(status)
        }.status == HttpStatusCode.Accepted
    }

    suspend fun updateAwayUntil(memberId: Long, awayUntil: LocalDateTime): Boolean {
        return client.patch(Member.Away(memberId = memberId)) {
            setBody(awayUntil)
        }.status == HttpStatusCode.Accepted
    }

    suspend fun postDepartment(department: Requests.Department): Responses.Department {
        return client.post(Department()) {
            setBody(department)
        }.body()
    }

    suspend fun call(call: Requests.Call): Responses.Call {
        return client.post(Call()) {
            setBody(call)
        }.body()
    }
}