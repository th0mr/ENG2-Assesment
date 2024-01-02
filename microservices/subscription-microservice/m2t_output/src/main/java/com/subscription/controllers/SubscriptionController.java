// protected region packageDefinition on begin
package todo
// protected region packageDefinition end

@Controller("/subscription")
public class SubscriptionController {

	// protected region classVariables on begin
	// Declare variables here...
	// protected region classVariables end

	@Post("TODO")
	public HttpResponse<Void> subscribeTo(long userId, long hashtagId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResponse<Void> unsubscribeFrom(long userId, long hashtagId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Post("TODO")
	public Iterable<Video> getTopTenVideos(long userId, long hashtagId) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

}