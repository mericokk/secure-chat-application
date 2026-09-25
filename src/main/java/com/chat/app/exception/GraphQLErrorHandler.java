package com.chat.app.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLErrorHandler extends DataFetcherExceptionResolverAdapter {

	@Override
	protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

		String message = ex.getMessage();

		if (ex instanceof UsernameTakenException || ex instanceof UserNotFoundException
				|| ex instanceof InvalidCredentialsException || ex instanceof NotGroupMemberException
				|| ex instanceof OwnerRequiredException || "GROUP_KEY_VERSION_OUTDATED".equals(message)
				|| "INVALID_KEY_VERSION".equals(message) || "USER_ALREADY_MEMBER".equals(message)
				|| "OWNER_CANNOT_REMOVE_SELF".equals(message)) {

			return GraphqlErrorBuilder.newError().message(message).path(env.getExecutionStepInfo().getPath())
					.location(env.getField().getSourceLocation()).errorType(ErrorType.BAD_REQUEST).build();
		}

		if ("RATE_LIMIT_EXCEEDED".equals(message) || ex instanceof RequestNotPermitted) {

			return GraphqlErrorBuilder.newError().message("Too many requests - Rate limit exceeded")
					.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation())
					.errorType(ErrorType.BAD_REQUEST).build();
		}

		return GraphqlErrorBuilder.newError().message(message != null ? message : "Internal server error")
				.path(env.getExecutionStepInfo().getPath()).location(env.getField().getSourceLocation())
				.errorType(ErrorType.INTERNAL_ERROR).build();
	}
}
