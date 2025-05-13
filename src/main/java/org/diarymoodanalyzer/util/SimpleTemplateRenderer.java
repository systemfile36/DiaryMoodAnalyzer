package org.diarymoodanalyzer.util;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 간단한 템플릿 렌더링 유틸 클래스
 */
public class SimpleTemplateRenderer {

    /**
     * 템플릿 문자열에서 "{var}" 패턴을 치환한다.
     * @param template 템플릿 String. e.g., "{user} leave comment to {diaryTitle}"
     * @param vars var Map. e.g., Map.of("user", "John", "diaryTitle", "My Diary")
     * @return render된 결과물
     */
    public static String render(String template, Map<String, String> vars) {
        if(template == null || vars == null || vars.isEmpty()) {
            return template;
        }

        StringBuilder rendered = new StringBuilder();

        for(int i = 0; i < template.length(); i++) {
            char current = template.charAt(i);

            if(current == '{') {
                int endBrace = template.indexOf('}');

                if(endBrace > i) {
                    rendered.append(vars.getOrDefault(
                            template.substring(i + 1, endBrace), //Get variable name
                            "" // default
                    ));
                    i = endBrace; // move to next character of end brace
                } else {
                    rendered.append(current);
                }
            } else {
                rendered.append(current);
            }
        }

        return rendered.toString();

    }
}
