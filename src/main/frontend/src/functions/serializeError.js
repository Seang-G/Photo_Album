export default function serializeError(error) {
  return {
    status: error.response.status,
    message: error.response.data,
  };
};