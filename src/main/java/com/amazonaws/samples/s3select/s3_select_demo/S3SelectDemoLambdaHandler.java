package com.amazonaws.samples.s3select.s3_select_demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Collections;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CSVInput;
import com.amazonaws.services.s3.model.CompressionType;
import com.amazonaws.services.s3.model.ExpressionType;
import com.amazonaws.services.s3.model.FileHeaderInfo;
import com.amazonaws.services.s3.model.InputSerialization;
import com.amazonaws.services.s3.model.JSONOutput;
import com.amazonaws.services.s3.model.OutputSerialization;
import com.amazonaws.services.s3.model.SelectObjectContentRequest;
import com.amazonaws.services.s3.model.SelectObjectContentResult;

public class S3SelectDemoLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

	private AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

	// retrieves bucket name and sample data from lambda environment variables.

	private static final String BUCKET_NAME = System.getenv("BUCKET_NAME");
	private static final String SAMPLE_DATA_KEY = System.getenv("SAMPLE_DATA");
	private static final String NAME = "name";
	private static final String LOCATION = "location";
	private static final String OCCUPATION = "occupation";

	// allow a-z or AZ or space character
	private static Pattern permittedCharacters = Pattern.compile("[a-zA-Z ]+");

	public S3SelectDemoLambdaHandler() {
	}

	@Override
	public ApiGatewayResponse handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		context.getLogger().log("Received event: " + event);

		// convert the event object to JSON
		JSONObject jsonObject = getEventData(event, context);

		if (jsonObject != null) {
			String name = (String) jsonObject.get(NAME);
			String location = (String) jsonObject.get(LOCATION);
			String occupation = (String) jsonObject.get(OCCUPATION);

			String query = buildQuery(name, location, occupation, context);

			context.getLogger().log("query is " + query);
			if (query != null) {
				SelectObjectContentRequest request = generateBaseJSONRequest(BUCKET_NAME, SAMPLE_DATA_KEY, query);

				try {

					SelectObjectContentResult result = s3Client.selectObjectContent(request);

					InputStream resultInputStream = result.getPayload().getRecordsInputStream();

					JSONParser jsonParser = new JSONParser();

					BufferedReader streamReader = new BufferedReader(new InputStreamReader(resultInputStream, "UTF-8"));

					JSONArray jsonArray = new JSONArray();
					String inputStr;

					while ((inputStr = streamReader.readLine()) != null) {

						JSONObject data = (JSONObject) jsonParser.parse(inputStr);
						jsonArray.add(data);

					}
					return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(jsonArray)
							.setHeaders(Collections.singletonMap("X-Powered-By", "AWS API Gateway & Lambda Serverless"))
							.build();

				} catch (ParseException e) {

					context.getLogger().log("unable to parse sample data from S3 " + e.getMessage());
				} catch (IOException ioe) {
					context.getLogger().log("IOException occured when reading input stream " + ioe.getMessage());
				} catch (Exception e) {
					context.getLogger().log("Exception occured " + e.getMessage());
					e.printStackTrace();

				}
			}
		}
		JSONArray invalidResponse = new JSONArray();
		invalidResponse.add("invalid input.  Request not processed. ");
		return ApiGatewayResponse.builder().setStatusCode(200).setObjectBody(invalidResponse)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS API Gateway & Lambda Serverless")).build();
	}

	private JSONObject getEventData(APIGatewayProxyRequestEvent event, Context context) {
		try {

			JSONParser requestParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) requestParser.parse(event.getBody());
			return jsonObject;
		}

		catch (ParseException e) {

			context.getLogger().log("unable to parse incoming event " + e.getMessage());
		}
		return null;
	}

	/**
	 * The purpose of this method is to build query based on the incoming event.
	 * Query is built based on the incoming field
	 * 
	 * @param name
	 * @param location
	 * @param occupation
	 * @param context
	 * @return - S3 Select Query
	 */
	private String buildQuery(String name, String location, String occupation, Context context) {

		// Input validation is performed to ensure only properly formed data is used.
		if (inputIsValid(name, location, occupation, context)) {
			if (name != null && location != null && occupation != null) {
				String query = "select * from s3object s where s.name like '%" + name + "%'" + " and s.City like '%"
						+ location + "%'" + " and s.Occupation like '%" + occupation + "%'";
				return query;
			} else if (name != null && location != null) {
				String query = "select * from s3object s where s.name like '%" + name + "%'" + " and s.City like '%"
						+ location + "%'";
				return query;
			} else if (occupation != null && location != null) {
				String query = "select * from s3object s where s.Occupation like '%" + occupation + "%'"
						+ " and s.City like '%" + location + "%'";
				return query;
			} else if (name != null && occupation != null) {
				String query = "select * from s3object s where s.Occupation like '%" + occupation + "%'"
						+ " and s.LastName like '%" + name + "%'";
				return query;
			} else if (name != null) {

				String query = "select * from s3object s where s.name like '%" + name + "%'";
				return query;
			} else if (location != null) {
				String query = "select * from s3object s where s.City like '%" + location + "%'";
				return query;
			} else if (occupation != null) {
				String query = "select * from s3object s where s.Occupation like '%" + occupation + "%'";
				return query;
			}
		}

		context.getLogger().log("Input validation occured, unable to build query");
		return null;
	}

	/**
	 * The purpose of this method is to ensure incoming data meets security
	 * requirements. Input validation is performed to ensure only properly formed
	 * data is entering the system. allows only string characters and space. Any
	 * other input is rejected.
	 * 
	 * @param name
	 * @param location
	 * @param occupation
	 * @param context
	 * @return
	 */
	private boolean inputIsValid(String name, String location, String occupation, Context context) {
		if (name != null && !permittedCharacters.matcher(name).matches()) {
			context.getLogger().log("Input validation failed for name attribute " + name);
			context.getLogger().log("input value must be in [a-zA-Z\\\\s]");
			return false;
		}
		if (location != null && !permittedCharacters.matcher(location).matches()) {
			context.getLogger().log("Input validation failed for location attribute " + location);
			context.getLogger().log("input value must be in [a-zA-Z\\\\s]");
			return false;
		}
		if (occupation != null && !permittedCharacters.matcher(occupation).matches()) {
			context.getLogger().log("Input validation failed for occupation attribute " + occupation);
			context.getLogger().log("input value must be in [a-zA-Z ]*");
			return false;
		}
		// otherwise, all attributes are either null or have passed input validation
		return true;

	}

	private static SelectObjectContentRequest generateBaseJSONRequest(String bucket, String key, String query) {
		SelectObjectContentRequest request = new SelectObjectContentRequest();
		request.setBucketName(bucket);
		request.setKey(key);
		request.setExpression(query);
		request.setExpressionType(ExpressionType.SQL);

		InputSerialization inputSerialization = new InputSerialization();
		CSVInput csvFile = new CSVInput();
		csvFile.setFileHeaderInfo(FileHeaderInfo.USE);
		inputSerialization.setCsv(csvFile);
		inputSerialization.setCompressionType(CompressionType.NONE);

		request.setInputSerialization(inputSerialization);
		OutputSerialization outputSerialization = new OutputSerialization();
		outputSerialization.setJson(new JSONOutput());

		request.setOutputSerialization(outputSerialization);

		return request;
	}
}