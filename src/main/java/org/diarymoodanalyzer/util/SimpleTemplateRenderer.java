package org.diarymoodanalyzer.util;

import java.util.Arrays;
import java.util.List;
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

    /**
     * 템플릿 문자열에서 "{var}" 패턴을 순서대로 치환한다.
     * @param template 템플릿 String. e.g., "{user} leave comment to {diaryTitle}"
     * @param values 치환할 값 리스트. 순서대로 치환됨
     * @return render된 결과물
     */
    public static String render(String template, List<String> values) {
        if (template == null || values == null || values.isEmpty()) {
            return template;
        }

        StringBuilder rendered = new StringBuilder();
        int valueIndex = 0;

        for (int i = 0; i < template.length(); i++) {
            char current = template.charAt(i);

            if (current == '{') {
                int endBrace = template.indexOf('}', i);
                if (endBrace > i) {
                    // 변수 이름을 무시하고 순서대로 치환
                    if (valueIndex < values.size()) {
                        rendered.append(values.get(valueIndex++));
                    } else {
                        rendered.append(""); // 값이 부족할 경우 빈 문자열로
                    }
                    i = endBrace; // } 이후로 이동
                } else {
                    rendered.append(current);
                }
            } else {
                rendered.append(current);
            }
        }

        return rendered.toString();
    }

    /**
     * 템플릿 문자열에서 "{var}" 패턴을 순서대로 치환한다.
     * @param template 템플릿 String. e.g., "{user} leave comment to {diaryTitle}"
     * @param values 치환할 값 리스트. 순서대로 치환됨
     * @return render된 결과물
     */
    public static String render(String template, String... values) {
        return render(template, Arrays.asList(values));
    }


}
