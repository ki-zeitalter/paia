export interface LlmModel {
  id: string;
  name: string;
  iconUrl: string;
  configured?: boolean;
}

export interface ModelConfig {
  modelId: string;
  apiKey: string;
} 