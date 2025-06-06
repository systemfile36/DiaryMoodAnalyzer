import { defineStore } from 'pinia'
import { ref, computed, isRef } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import TokenUtils from '../utils/TokenUtils'
import Authority from '../utils/Authority'

/**
 * 인증 정보와 관련 유틸 함수를 가진 store
 */
export const useAuthManagerStore = defineStore('authManager', () => {
    const loginUrl = '/api/auth/login'
    const logoutUrl = '/api/logout'
    const signUpUrl = '/api/auth/signup'
    const refreshTokenUrl = '/api/token'
    const expertPrefix = '/api/expert'

    const SEND_VERIFICATION_URL = '/api/email/send/verification-code'
    const VERIFY_CODE_URL = '/api/email/verify'

    const TOKEN_PREFIX = 'Bearer '

    //store에서 router를 사용하기 위함
    const router = useRouter();

    const userName = ref("");
    const role = ref("");

    /** @type {Ref<Date>} */
    const accessTokenExp = ref(new Date(0));

    /** @type {Ref<Date>} */
    const refreshTokenExp = ref(new Date(0));

    /** @type {Ref<boolean>} */
    const isAuthenticated = ref(false);

    /**
     * 
     * @param {{email: string, password: string}} credentials 
     * @param {(err)=>void} onFailure handler will be executed when failure
     */
    async function login(credentials, 
        onFailure=(err)=>{console.log("Fail on login : " + err)}) {
        await axios.post(loginUrl, credentials, 
            {
                headers: getDefaultHeaders()
            }
        ).then((res)=>{
            TokenUtils.setAccessToken(res.data['accessToken']);
            TokenUtils.setRefreshToken(res.data['refreshToken']);
            initStates();
            
            //임시로 권한에 따른 홈 페이지 리다이렉트 구현 

            const redirectUrl = role.value == Authority.EXPERT ? "/expertPage" : "/"

            router.push(redirectUrl);
        }).catch((error) => {
            onFailure(error);
        })
    }

    /**
     * Send verification code to `email`
     * @param {string} email email will receive verification code
     * @param {()=>void} onSuccess handler will be executed when success
     * @param {()=>void} onFailure handler will be executed when failure
     */
    async function sendVerificationCode(email, 
        onSuccess=()=>{console.log("Successfully send verification code")}, 
        onFailure=()=>{console.log("Fail on send verification code")}
    ) {
        await axios.post(SEND_VERIFICATION_URL, {
            email: email
        }, {
            headers: getDefaultHeaders()
        }).then((res) => {
            onSuccess();
        }).catch((err) => {
            console.log(err);
            onFailure();
        })
    }

    /**
     * Verify email verification code. 
     * @param {string} email email to verify
     * @param {string} code verification code that received
     * @param {()=>void} onSuccess handler will be executed when success
     * @param {()=>void} onFailure handler will be executed when failure
     * @param {()=>void} onFailCountExceed handler will be executed when fail count exceed
     */
    async function verifyCode(email, code, 
        onSuccess=()=>{console.log("Successfully verified email")}, 
        onFailure=()=>{console.log("Fail on verify email")}, 
        onFailCountExceed=()=>{console.log("Fail count exceed. re-send verification code")}
    ) {
        await axios.post(VERIFY_CODE_URL, {
            email: email,
            code: code
        }, {
            headers: getDefaultHeaders()
        }).then((res) => {
            console.log(res);

            if(res.data['verified']) {
                onSuccess();
            } else {
                onFailure();
            }
        }).catch((err) => {
            const resBody = err.response.data;

            if(resBody['isFailCountExceed']) {
                onFailCountExceed();
            } else {
                onFailure();
            }
        })
    }

    /**
     * 회원가입 요청을 보낸다. authority는 포함하지 않으면 `ROLE_USER`로 작동한다.
     * @param {{email: string, password: string, authority: string}} info 
     * @param {(res)=>void} onSuccess handler will be executed when success
     * @param {(err)=>void} onFailure handler will be executed when failure
     */
    async function signUp(info, 
        onSuccess=(_)=>{console.log("Successfully send signUp request")}, 
        onFailure=(err)=>{console.log("Fail on send signUp request : " + err)}
    ) {
        await axios.post(signUpUrl, info, 
            {
                headers: getDefaultHeaders()
            }
        ).then((res)=>{
            console.log(res.status);
            // alert('회원가입에 성공하였습니다.');
            // router.push('/');
            onSuccess(res);
        }).catch((error)=>{
            console.log(error);
            onFailure(error);
        })
    }

    /**
     * 로그아웃 요청을 보낸다. 
     * 요청 성공 여부에 상관없이 인증 정보를 제거한다.
     */
    async function logout() {

        await axios.get(logoutUrl, 
            {
                headers: getDefaultHeaders()
            }
        ).catch((error)=>{
            console.log(error);
        }).finally(()=>{
            //로그아웃 요청 성공 여부에 상관없이 인증 정보 제거
            TokenUtils.deleteTokens();
            isAuthenticated.value = false;
            router.push('/');

            //refresh page
            router.go(0);
        })
    }

    /**
     * 현재 접속중인 전문가가 관리하는 사용자 목록을 반환한다.
     * @returns {Promise<Array<string>> | Promise<undefined>} - 관리하는 사용자들의 이메일. 오류 발생 시 undefined
     */
    async function getManagedUsers() {
        return await axios.get(expertPrefix + "/managedUsers", {
            headers: getDefaultHeaders()
        }).then((res) => {
            console.log(res);
            return res.data;
        }).catch((error) => {
            console.log(error);
            return undefined;
        })
    }

    /**
     * 토큰이 정상적으로 사용가능한지 여부를 반환한다.
     * 토큰들의 만료 여부를 체크하고 만료되었다면 재발급을 시도한다. 
     * 
     * 로직을 거친 후, 토큰이 사용가능하다면 true, 아니라면 false 반환한다.
     * @returns { Promise<boolean> } 토큰이 사용가능한지 여부
     */
    async function checkTokens() {
        //리프레쉬 토큰이 만료되었다면 false
        if(isRefreshTokenExpired()) {
            return false;
        }

        //엑세스 토큰 만료 시, 재발급
        if(isAccessTokenExpired()) {
            const isSuccess = await refreshAccessToken();
            return isSuccess;
        }

        return true;
    } 

    /**
     * 엑세스 토큰을 재발급 받는 메소드
     * @returns { Promise<boolean> } 토큰 재발급이 성공적이라면 true, 아니라면 false 반환
     */
    async function refreshAccessToken() {
        
        try {
            const response = await axios.post(refreshTokenUrl, {
                refreshToken: TokenUtils.getRefreshToken()
            }, {
                headers: getDefaultHeaders()
            })

            //Distructuring assingnment
            const { accessToken } = response.data;

            TokenUtils.setAccessToken(accessToken);
            accessTokenExp.value = TokenUtils.getExp(accessToken);

            return true;
            
        } catch(error) {
            console.log(error);
            return false;
        }

    }

    /**
     * 저장된 토큰을 통해 인증 정보들을 store에 세팅
     */
    async function initStates() {
        try {
            const accessToken = TokenUtils.getAccessToken();
            const refreshToken = TokenUtils.getRefreshToken();

            //인증 정보들을 토큰에서 추출해서 세팅
            if(accessToken !== null) {
                const claim = TokenUtils.getClaim(accessToken);
                userName.value = claim['sub'];
                role.value = claim['role'];
                accessTokenExp.value = new Date(claim['exp'] * 1000);
            }

            if(refreshToken !== null) {
                refreshTokenExp.value = TokenUtils.getExp(refreshToken);
            }

            //토큰들이 사용 불가하면 삭제하고 인증 정보 초기화
            if(!(await checkTokens())) {
                TokenUtils.deleteTokens();
                isAuthenticated.value = false;
            } else {
                isAuthenticated.value = true;
            }

        } catch (error) {
            console.log(error);
        }
    }

    /**
     * 
     * @returns { boolean } 엑세스 토큰이 만료되었는지 여부
     */
    function isAccessTokenExpired() {
        return accessTokenExp.value.getTime() < Date.now();
    }

    /**
     * 
     * @returns { boolean } 리프레쉬 토큰이 만료되었는지 여부
     */
    function isRefreshTokenExpired() {
        return refreshTokenExp.value.getTime() < Date.now();
    }

    /**
     * 
     * @returns { Object } 인증값이 포함된 기본적인 헤더
     */
    function getDefaultHeaders() {
        return {
            "Authorization" : TOKEN_PREFIX + TokenUtils.getAccessToken(),
            "Content-Type": 'application/json'
        }
    }

    return { 
        userName, 
        role, 
        accessTokenExp,
        refreshTokenExp,
        isAuthenticated, 
        login,
        logout,
        signUp,
        refreshAccessToken,
        sendVerificationCode,
        verifyCode,
        isAccessTokenExpired,
        isRefreshTokenExpired,
        checkTokens,
        initStates,
        getDefaultHeaders,
        getManagedUsers,
    }

})

