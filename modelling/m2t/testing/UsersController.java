// protected region packageDefinition on begin
package todo
// protected region packageDefinition end

@Controller("/users")
public class UsersController {

	// protected region classVariables on begin
	// Declare variables here...
	// protected region classVariables end

	@Get("TODO")
	public Iterable<User> list() {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Post("TODO")
	public HttpResonse<Void> add(UserDTO userDetails) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public User getUser(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Put("TODO")
	public HttpResonse<Void> updateUser(long id, UserDTO userDetails) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Delete("TODO")
	public HttpResponse<Void> deleteUser(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<Video> getVideos(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<Video> getLikedVideos(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<Video> getDislikedVideos(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

	@Get("TODO")
	public Iterable<Video> getWatchedVideos(long id) {
		// protected region methodContents on begin
		System.out.println('Method is not implemented');
		// protected region methodContents end
	}

}