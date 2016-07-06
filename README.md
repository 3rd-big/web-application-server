# feature/config-bad-version을 위한 README.md
## 목적
* 기존의 웹 서버 코드 리팩토링

## 방안
#### if/else로 넘쳐나던 리퀘스트 요청을 어떻게 처리할 것인가? <br/>
 * @RequestMapping 를 생성하여 모든 리퀘스트 처리 메소드 value와 함께 할당한다. <br/>
 * WebServer Class에 method factory라는 static Map을 생성한다. method factory는 웹서버가 올라갈때 한번만 등록해주면 되므로 WebServer의 static으로 미리 객체 생성한다.<br/>
 * Controller에 있는 모든 메소드를 method factory에 등록한다. <br/>
 * method factory의 형태는 아래와 같다. <br/>
 { key : @RequestMapping의 value (즉, url), value : 해당 어노테이션의 method } <br/>
 * 이후에는 request마다 url을 key로 하여 method factory에서 꺼내와서 invoke(메소드 실행) 시키면 끝 <br/>
 
#### RequestHandler에 많은 코드 할당된것은 어떻게 분리할 것인가? <br/>
 * HttpRequest, HttpResponse Class를 생성하여 각각의 목적에 맞게 코드 분리
 * Controller와 Service Class를 생성하여 각각의 목적에 맞게 코드 분리
 * Controller와 Service는 request마다 객체 생성할 필요 없으니 웹서버가 올라갈때 한번만 생성되도록 WebServer의 static으로 미리 객체 생성(이후에 Spring처럼 싱글톤 객체로 수정예정) 