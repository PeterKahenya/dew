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
        println("AuthRepositoryImpl Auth")
        val localAuth = ds.getAuth()
        println("AuthRepositoryImpl LocalAuth: $localAuth")
        if (localAuth != null){
            println("AuthRepositoryImpl Auth not null")
            return localAuth
        }else{
            println("AuthRepositoryImpl No LocalAuth, you may want to redirect to login screen")
            return null
        }
    }

    override suspend fun login(userLogin: UserLogin): Auth {
        println("AuthRepositoryImpl Login Request: $userLogin.email")
        val loginResponse = api.login(userLogin)
        if (loginResponse.message == "Logged In"){
            val userProfile = api.profile()
            val auth = Auth(userProfile.id)
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
        api.logout()
    }

}