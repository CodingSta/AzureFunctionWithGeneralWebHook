## 이 Android App 용 Source 는 Microsoft Azure 의 Functions 템플릿 중, General Web Hook 템플릿을 간단히 테스트해보실 수 있는 소스 입니다.

## 다른 Web Hook 템플릿을 이용하실 때는, REST API로 POST 하는 JSON Format 만 맞추시면 됩니다.

## Functions REST API Request 에 대한 응답시간은 첫 실행 때엔 조금 걸리지만, 자주 실행하게되면 아주 빠릅니다. ^^

## 사용 방법은 아래와 같습니다.
<li>MainActivity의 query 와 url 값을 자신이 만든 Functions 의 Endpoint 에 있는 값으로 바꾼다. </li>
<li>위의 Endpoint 를 얻으려면 Functions 를 하나 만든 이후,  화면 우상단 즉 — Save, Run 버튼 오른쪽 Get function URL 링크를 누르시면 됩니다.</li>


## Functions 의 General Web Hook 템플릿의 C# 소스는 아래와 같습니다. 자신이 만든 Functions 의 소스와 비교해보시면 오류날 일이 줄어들 것입니다.

public static async Task<object> Run(HttpRequestMessage req, TraceWriter log)
{
    log.Info($"Webhook was triggered!");

    string jsonContent = await req.Content.ReadAsStringAsync();
    dynamic data = JsonConvert.DeserializeObject(jsonContent);

    if (data.first == null || data.last == null) {
        return req.CreateResponse(HttpStatusCode.BadRequest, new {
            error = "Please pass first/last properties in the input object"
        });
    }

    return req.CreateResponse(HttpStatusCode.OK, new {
        greeting = $"Hello {data.first} {data.last}!"
    });
}
