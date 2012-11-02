package impossible.apps;

public class ResultBind<PROBLEM, RESULT> {
	private final PROBLEM problem;
	private final RESULT result;

	public ResultBind(PROBLEM problem, RESULT result) {
		super();
		this.problem = problem;
		this.result = result;
	}

	public PROBLEM getProblem() {
		return problem;
	}

	public RESULT getResult() {
		return result;
	}

}
