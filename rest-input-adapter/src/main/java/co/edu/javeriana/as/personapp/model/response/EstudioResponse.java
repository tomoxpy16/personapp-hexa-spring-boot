package co.edu.javeriana.as.personapp.model.response;

import co.edu.javeriana.as.personapp.model.request.EstudioRequest;

public class EstudioResponse extends EstudioRequest {

	private String status;

	public EstudioResponse(String professionId, String personId, String graduationDate, String universityName, String database, String status) {
		super(professionId, personId, graduationDate, universityName, database);
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
