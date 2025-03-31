package dew.app.mobile.data.repository

import dew.app.mobile.data.model.Auth
import dew.app.mobile.data.model.User
import dew.app.mobile.data.model.UserLogin
import dew.app.mobile.data.model.UserSignup
import dew.app.mobile.data.source.DewApi
import dew.app.mobile.data.source.DewDataStore
import javax.inject.Inject

interface AuthRepository{
    suspend fun auth(): Auth?
    suspend fun login(userLogin: UserLogin): Auth
    suspend fun signup(userSignup: UserSignup): User
    suspend fun logout()
}

class AuthRepositoryImpl @Inject constructor(
    private val api: DewApi,
    private val ds: DewDataStore
): AuthRepository {
    override suspend fun auth(): Auth? {
        return ds.getAuth()
    }

    override suspend fun login(userLogin: UserLogin): Auth {
        val loginResponse = api.login(userLogin)
        if (loginResponse.message == "Logged In"){
            val userProfile = api.profile("Bearer ${loginResponse.accessToken}")
            val auth = Auth(
                userId = userProfile.id,
                email = userProfile.email,
                accessToken = loginResponse.accessToken,
                name = userProfile.name
            )
            ds.saveAuth(auth)
            return auth
        }else{
            throw Exception("Login Failed")
        }
    }

    override suspend fun signup(userSignup: UserSignup): User {
        return api.signup(userSignup)
    }

    override suspend fun logout() {
        val localAuth = ds.getAuth()
        if (localAuth == null){
            throw Exception("No Local Auth")
        }else{
            api.logout("Bearer ${localAuth.accessToken}")
        }
    }

}