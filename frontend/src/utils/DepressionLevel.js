export default class DepressionLevel {
    /**
     * depressionLevel이 1보다 작을 때, 오류임을 나타냄
     * @type {Array<{status: string, icon: string}>}
     */
    static errors = {
        '-2': { status: "오류", icon: "fa-solid fa-triangle-exclamation" },
        '-1': { status: "측정 중", icon: "fa-regular fa-circle" }
    }

    /**
     * depressionLevel에 따른 상태와 아이콘의 쌍.
     * @type {Array<{status: string, icon: string}>}
     */
    static values = [
        { status: "아주 좋음", icon: "fa-regular fa-face-laugh-squint"},
        { status: "좋음", icon: "fa-regular fa-face-smile"},
        { status: "좋지 않음", icon: "fa-regular fa-face-meh"},
        { status: "우울함", icon: "fa-regular fa-face-frown"}
    ]

    /**
     * depressionLevel을 받아서, 그에 맞는 상태와 문자열을 반환한다. 
     * @param {number} depressionLevel Diary의 depressionLevel 
     * @returns {{status: string, icon: string}} 해당 수치에 맞는 상태 문자열과 아이콘 이름
     */
    static getValue(depressionLevel) {
        if(depressionLevel < 0) {
            return this.errors[depressionLevel];
        } else if(depressionLevel > 10) {
            return this.errors['-2'];
        } else {
            //인덱스를 0 ~ 3으로 변환 
            const index = Math.floor(depressionLevel / 3);
            return this.values[index];
        }
    }

}