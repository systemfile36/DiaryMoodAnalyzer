<template>
    <!-- height: 100vh;-->
    <div id="front-page" class="d-flex flex-grow-1 justify-content-center vh-100 w-100" >
        <div id="logo-wrapper" class="logo-wrapper 
            d-flex align-items-center justify-content-center">
            <div class="logo d-flex align-items-center justify-content-center flex-column">
                <svg viewBox="0 0 24 24" aria-hidden="true" class="svg">
                    <g>
                        <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"></path>
                    </g>
                </svg>
            </div>
        </div>
        <div class="info-wrapper d-flex flex-column align-items-center justify-content-center">
            <h1 class="display-3">DiaryMoodAnalyzer</h1>
            <span>지금 가입해보세요!</span>
            <div class="d-flex flex-column">

            </div>
        </div>
    </div>
    
</template>
<script setup>



</script>
<style scoped lang="scss">

    //850px 이하일 때, 로고 이동 
    @media (max-width: 850px) {
        .logo-wrapper {
            display: none !important;
        }


        
    }

    .info-wrapper {
        padding: 16px;
        text-overflow: unset;
    }

    .logo-wrapper {
        width: 50%;
        height: 100%;

        
    }

    .logo {
        width: 100%;
        height: 100%;
    }

    .svg {
        position: relative;
        max-height: 380px;
        max-width: 100%;

        height: 50%;
        padding: 32px;
    }
        
</style>