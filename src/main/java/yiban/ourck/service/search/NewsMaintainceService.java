package yiban.ourck.service.search;

public interface NewsMaintainceService {
	void coreDBSync(String coreName, String mask) throws Exception;
	void cluster();
}
