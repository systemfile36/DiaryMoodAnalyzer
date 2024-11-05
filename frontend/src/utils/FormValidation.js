import { ref, watch } from 'vue'

/**
 * 폼에서 필드의 유효성을 검사하는 컴포저블 함수
 * 
 * @param { Array<Object> } fields - ref와 유효성 rule들을 담은 배열
 * @param { Object } fields[].field - vue의 ref 객체
 * @param { string } fields[].fieldName - 필드의 이름
 * @param { Array<Object> } fields[].rules - 유효성 rule들의 배열
 * @param { Function } fields[].rules[].validate - value를 받아 유효성을 boolean으로 반환하는 함수
 * @param { string } fields[].rules[].message - errors에 저장될 에러 메시지
 * @returns { Object } [{필드명: [message, ...]}, ...]의 형태로 반환되는 에러 목록
 */
export function useFormValidation(fields) {

    //오류 목록이 저장될 배열
    const errors = ref([]);

    //인자로 받은 필드들에 대해 검사
    fields.forEach(({ field, fieldName, rules }) => {
        //watch 각 필드에 감시 할당
        watch(field, () => {
            const fieldErrors = [];

            //해당 필드의 rules를 순회하며 검사
            //유효하지 않으면 해당 필드의 오류 목록에 추가
            rules.forEach(({ validate, message }) => {
                if(!validate(field.value)) {
                    fieldErrors.push(message);
                }
            })

            //필드 내에 에러가 없으면 errors에서 프로퍼티 제거
            // 그렇지 않으면 에러 메시지 배열 추가 
            if(fieldErrors.length === 0) {
                delete errors.value[fieldName];
            } else {
                errors.value[fieldName] = fieldErrors;
            }
        });
    });

    /**
     * 필드를 검사하고 에러 메시지를 추가하는 동작을 수동으로 실행한다. 
     * 즉, field의 수정 없이 강제로 실행한다.
     * (closure를 통해 매개 변수 fields에 접근 가능)
     */
    function checkRules() {
        fields.forEach(({field, fieldName, rules}) => {

            const fieldErrors = [];

            //해당 필드의 rules를 순회하며 검사
            //유효하지 않으면 해당 필드의 오류 목록에 추가
            rules.forEach(({ validate, message }) => {
                if(!validate(field.value)) {
                    fieldErrors.push(message);
                }
            })

            //필드 내에 에러가 없으면 errors에서 프로퍼티 제거
            // 그렇지 않으면 에러 메시지 배열 추가 
            if(fieldErrors.length === 0) {
                delete errors.value[fieldName];
            } else {
                errors.value[fieldName] = fieldErrors;
            }
        })
    }

    return { errors, checkRules };
}